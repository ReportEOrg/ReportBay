package org.reporte.model.service.util;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

public class SelectFieldMatcher implements SelectItemVisitor{

	private SelectItem matchedSelectItem = null;
	private String aliasName = null;
	
	public SelectFieldMatcher(String aliasName){
		this.aliasName = aliasName;
	}
	
	@Override
	public void visit(AllColumns allColumns) {
		//does not handle all columns
		
	}

	@Override
	public void visit(AllTableColumns allTableColumns) {
		//does not handle all table columns
		
	}

	@Override
	public void visit(SelectExpressionItem selectExpressionItem) {
		Alias alias = selectExpressionItem.getAlias();
		
		String columnName = null;
		
		//have alias
		if(alias !=null){
			columnName = alias.getName();
		}
		else{
			Expression expr = selectExpressionItem.getExpression();
			
			if(expr instanceof Column){
				columnName = ((Column) expr).getColumnName();
			}
		}
		
		if(columnName!=null){

			//remove ` character
			columnName = columnName.replace("`", "");

			if(aliasName.equals(columnName)){
				matchedSelectItem = selectExpressionItem;
			}
		}
	}

	/**
	 * @return the matchedSelectItem
	 */
	public SelectItem getMatchedSelectItem() {
		return matchedSelectItem;
	}

	/**
	 * @param matchedSelectItem the matchedSelectItem to set
	 */
	public void setMatchedSelectItem(SelectItem matchedSelectItem) {
		this.matchedSelectItem = matchedSelectItem;
	}
	
}