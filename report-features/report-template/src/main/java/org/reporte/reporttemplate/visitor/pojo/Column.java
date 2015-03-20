package org.reporte.reporttemplate.visitor.pojo;

import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.schema.Table;

import org.reporte.reporttemplate.service.impl.ExpressionType;

public class Column {

	private String name;
	private String alias;
	private boolean useAs = false;
	private Table table;
	private String fullyQualifiedName;
	private boolean allColumn = false;
	private boolean allTableColumn = false;
	private ExpressionType expression;
	private Function function;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public boolean isUseAs() {
		return useAs;
	}
	public void setUseAs(boolean useAs) {
		this.useAs = useAs;
	}
	public Table getTable() {
		return table;
	}
	public void setTable(Table table) {
		this.table = table;
	}
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}
	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}
	public boolean isAllColumn() {
		return allColumn;
	}
	public void setAllColumn(boolean allColumn) {
		this.allColumn = allColumn;
	}
	public boolean isAllTableColumn() {
		return allTableColumn;
	}
	public void setAllTableColumn(boolean allTableColumn) {
		this.allTableColumn = allTableColumn;
	}
	public ExpressionType getExpression() {
		return expression;
	}
	public void setExpression(ExpressionType expression) {
		this.expression = expression;
	}
	public Function getFunction() {
		return function;
	}
	public void setFunction(Function function) {
		this.function = function;
	}
	@Override
	public String toString() {
		return "Column [name=" + name + ", alias=" + alias + ", useAs=" + useAs
				+ ", table=" + table + ", fullyQualifiedName="
				+ fullyQualifiedName + ", allColumn=" + allColumn
				+ ", allTableColumn=" + allTableColumn + ", expression="
				+ expression + ", function=" + function + "]";
	}
	
}
