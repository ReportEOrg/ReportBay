package org.reporte.model.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.expression.Alias;
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
	private Map<String, String> processAliasMap = new HashMap<String, String>();
	
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
		
		FromItem fromItem = plainSelect.getFromItem();
		
		fromItem.accept(this);
		
		for(Join joinitem: plainSelect.getJoins()){
			fromItem = joinitem.getRightItem();
			fromItem.accept(this);
		}

		List<SelectItem> newSelectItemList = new ArrayList<SelectItem>();		
		List<SelectItem> selectItemList = plainSelect.getSelectItems();
		
		for (SelectItem selectItem : selectItemList) {
			selectItem.accept(this);
			
			if(selectItem instanceof SelectExpressionItem){
				newSelectItemList.add(selectItem);
			}
		}

		int idx=0;
		for(Map.Entry<String, String> entry: processAliasMap.entrySet()){
			try {
				String aliasName = entry.getKey();
				List<ColumnMetadata> results = jdbcClient.getColumnsFromQuery(datasource, entry.getValue());

				for(ColumnMetadata columnMetadata: results){
					SelectExpressionItem sei = new SelectExpressionItem();
					
					//1. construct expression part
					Column expr = new Column();
					expr.setColumnName(columnMetadata.getLabel());
					
					Table table = new Table(new Database(null,null),null, aliasName);
					expr.setTable(table);

					sei.setExpression(expr);
					
					//2. construct alias part
					Alias alias = new Alias(aliasName.replace(".", "_")+"_"+idx+"_"+columnMetadata.getLabel(), true);
					
					sei.setAlias(alias);
					
					newSelectItemList.add(sei);
					
					idx++;
				}
				
			} catch (JdbcClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//replace the selectItemList
		plainSelect.setSelectItems(newSelectItemList);
		
		//construct the query
		convertedQuery = plainSelect.toString();
	}
	
	@Override
	public void visit(SetOperationList setOperationList) {
		//TODO: no javadoc info on this?
		
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
			processAliasMap.put(allTableAliasName,alias);
		}
		//belongs to actual table, use "select * from table" for obtain the column name
		else{
			String refAliasName = (StringUtils.isBlank(table.getSchemaName())?"":table.getSchemaName()+".")+allTableAliasName;
			processAliasMap.put(refAliasName, "Select * from "+refAliasName);
		}
	}

	@Override
	public void visit(SelectExpressionItem selectExpressionItem) {
		//do nothing for expression type select item
		
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