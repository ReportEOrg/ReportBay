package org.reporte.model.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Database;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;

import org.apache.commons.lang3.StringUtils;
import org.reporte.datasource.domain.ColumnMetadata;
import org.reporte.datasource.domain.Datasource;
import org.reporte.datasource.service.JdbcClient;
import org.reporte.datasource.service.exception.JdbcClientException;

public class JoinQueryConverter implements SelectVisitor, SelectItemVisitor, FromItemVisitor{
	
	private Map<String, String> fromAliasLookupMap = new HashMap<String, String>();
	private List<SelectItem> newSelectItemList = new ArrayList<SelectItem>();
	private List<String> processedColumnNameList = new ArrayList<String>();
	private int position;
	
	private JdbcClient jdbcClient;
	private Datasource datasource;
	
	private String convertedQuery=null;
	
	/**
	 * 
	 * @param datasource
	 * @param jdbcClient
	 */
	public JoinQueryConverter(Datasource datasource, JdbcClient jdbcClient){
		this.datasource = datasource;
		this.jdbcClient = jdbcClient;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getConvertedQuery(){
		return convertedQuery;
	}
	
	/*** select visitor **/
	@Override
	public void visit(PlainSelect plainSelect) {
		
		handlePlainSelectVisitor(plainSelect);
		
		//construct the query
		convertedQuery = plainSelect.toString();
	}
	
	/**
	 * 
	 * @param plainSelect
	 */
	private void handlePlainSelectVisitor(PlainSelect plainSelect){

		fromAliasLookupMap.clear();
		newSelectItemList.clear();
		processedColumnNameList.clear();
		position = 1;
		
		FromItem fromItem = plainSelect.getFromItem();
		
		fromItem.accept(this);
		
		if(plainSelect.getJoins()!=null){
			for(Join joinitem: plainSelect.getJoins()){
				fromItem = joinitem.getRightItem();
				fromItem.accept(this);
			}
		}

		List<SelectItem> selectItemList = plainSelect.getSelectItems();
		
		if(selectItemList!=null){
			for (SelectItem selectItem : selectItemList) {
				selectItem.accept(this);
			}
		}

		//replace the selectItemList
		plainSelect.setSelectItems(newSelectItemList);
	}
	
	/**
	 * 
	 * @param aliasRef
	 * @param query
	 */
	private void expandAllTableSelectItem(String aliasRef, String query){
		String processColumnName;
		try {
			List<ColumnMetadata> results = jdbcClient.getColumnsFromQuery(datasource, query);
			
			//for each expanded column
			for(ColumnMetadata columnMetadata: results){
				
				String columnName = columnMetadata.getLabel();
				
				//create a correspond select expression item
				SelectExpressionItem sei = new SelectExpressionItem();
				//add to new list
				newSelectItemList.add(sei);

				//setup expression column
				Column expr = new Column();
				expr.setColumnName(columnName);
				
				Table table = new Table(new Database(null,null),null, aliasRef);
				expr.setTable(table);

				sei.setExpression(expr);
				
				//if columnName exists in the past, need to create as alias
				if(processedColumnNameList.contains(columnName)){
					processColumnName = columnName+"_"+position;
					//construct alias part
					Alias exprAlias = new Alias(processColumnName, true);
					
					//converted expression to expression with alias
					sei.setAlias(exprAlias);
				}
				else{
					processColumnName = columnName;
				}
				
				//added process column name to track list
				processedColumnNameList.add(processColumnName);
				
				//increment processing field position
				position++;
			}
		} catch (JdbcClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * UNION is handled here 
	 */
	@Override
	public void visit(SetOperationList setOperationList) {
		List<PlainSelect> plainSelectList = setOperationList.getPlainSelects();
		
		for(PlainSelect plainSelect: plainSelectList){
			handlePlainSelectVisitor(plainSelect);
		}
		
		//construct the query
		convertedQuery = setOperationList.toString();
	}

	@Override
	public void visit(WithItem withItem) {
		//TODO: do nothing for with Item Select visitor?
		
	}

	/*** selectItem visitor **/
	@Override
	public void visit(AllColumns allColumns) {
		//do nothing, as "select *" can be handled properly without need to be converted 
	}

	@Override
	public void visit(AllTableColumns allTableColumns) {
		Table table = allTableColumns.getTable();
		
		//alias or table name of all table columns (e.g. a.*)
		String allTableAliasName = table.getName();
		
		//check if name belongs to alias instead of actual table
		String alias = fromAliasLookupMap.get(allTableAliasName);
		
		if(alias!=null){
			expandAllTableSelectItem(allTableAliasName,alias);
		}
		else{
			String refAliasName = (StringUtils.isBlank(table.getSchemaName())?"":table.getSchemaName()+".")+allTableAliasName;
			expandAllTableSelectItem(refAliasName, "Select * from "+refAliasName);
		}
	}

	@Override
	public void visit(SelectExpressionItem selectExpressionItem) {
Alias alias = selectExpressionItem.getAlias();
		
		String columnName;
		
		//have alias
		if(alias !=null){
			columnName = alias.getName();
			
			if(processedColumnNameList.contains(columnName)){
				//replace by position alias
				columnName = columnName+"_"+position;
				alias.setName(columnName);
			}

			//include the new columnName 
			processedColumnNameList.add(columnName);
		}
		//expression without alias
		else{
			Expression expr = selectExpressionItem.getExpression();
			
			//for function type expression, create a alias and assigned
			if(expr instanceof Function){
				columnName = ((Function) expr).getName()+"_"+position;
				
				//construct alias part
				Alias exprAlias = new Alias(columnName, true);
				
				//converted expression to expression with alias
				selectExpressionItem.setAlias(exprAlias);
				
				//include the new columnName 
				processedColumnNameList.add(columnName);
			}
			else if(expr instanceof Column){
				columnName = ((Column) expr).getColumnName();
				
				if(processedColumnNameList.contains(columnName)){
					//replace by position alias
					columnName = columnName+"_"+position;
					
					//construct alias part
					Alias exprAlias = new Alias(columnName, true);
					
					//converted expression to expression with alias
					selectExpressionItem.setAlias(exprAlias);
				}
				
				//include the new columnName 
				processedColumnNameList.add(columnName);
			}
		}
		
		newSelectItemList.add(selectExpressionItem);
		position++;
		
	}
	/*** fromItem visitor **/

	@Override
	public void visit(Table tableName) {
		//from item is a sub select
		Alias fromAlias = tableName.getAlias();
		
		//put the alias as lookup for select query
		if(fromAlias!=null){
			String refAliasName = (StringUtils.isBlank(tableName.getSchemaName())?"":tableName.getSchemaName()+".")+tableName.getName();
			fromAliasLookupMap.put(fromAlias.getName(), "select * from "+refAliasName);
		}
		
	}

	@Override
	public void visit(SubSelect subSelect) {
		//from item is a sub select
		Alias fromAlias = subSelect.getAlias();
		
		//put the alias as lookup for subselect query
		if(fromAlias!=null){
			fromAliasLookupMap.put(fromAlias.getName(), subSelect.getSelectBody().toString());
		}
		
	}

	@Override
	public void visit(SubJoin subjoin) {
		// TODO need to handle sub join?
		
	}

	@Override
	public void visit(LateralSubSelect lateralSubSelect) {
		// TODO no javadoc info on this
		
	}

	@Override
	public void visit(ValuesList valuesList) {
		// TODO no javadoc info on this
		
	}
}