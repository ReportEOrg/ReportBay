package org.reportbay.reporttemplate.visitor;

import java.util.List;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

import org.apache.commons.lang3.EnumUtils;
import org.reportbay.reporttemplate.service.impl.ExpressionType;
import org.reportbay.reporttemplate.visitor.pojo.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectItemVisitorImpl implements SelectItemVisitor {
	
	private static final Logger LOG = LoggerFactory.getLogger(SelectItemVisitorImpl.class);
	
	private List<Column> columns;

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	@Override
	public void visit(AllColumns allColumns) {
		LOG.info("AllColumns [{}]",allColumns);
		Column column = new Column();
		column.setAllColumn(true);
		columns.add(column);
	}

	@Override
	public void visit(AllTableColumns allTableColumns) {
		LOG.info("AllTableColumns [{}]",allTableColumns);
		Column column = new Column();
		column.setTable(allTableColumns.getTable());
		column.setAllTableColumn(true);
		columns.add(column);
	}

	@Override
	public void visit(SelectExpressionItem selectExpressionItem) {
		LOG.info("SelectExpressionItem [{}]",selectExpressionItem);
		// Set the expression type so we can use the value while scanning through the list of column names
		ExpressionType expressionType = EnumUtils.getEnum(ExpressionType.class, selectExpressionItem.getExpression().getClass().getSimpleName());
		Column column = new Column();
		column.setExpression(expressionType);
		Alias alias = selectExpressionItem.getAlias();
		if (alias!=null) {
			column.setAlias(alias.getName());
			column.setUseAs(alias.isUseAs());
		}
		Expression expression = selectExpressionItem.getExpression();
		if (expression!=null) {
			if (expression instanceof net.sf.jsqlparser.schema.Column) {
				net.sf.jsqlparser.schema.Column jsqlColumn = (net.sf.jsqlparser.schema.Column) expression;
				column.setName(jsqlColumn.getColumnName());
				column.setFullyQualifiedName(jsqlColumn.getFullyQualifiedName());
				column.setTable(jsqlColumn.getTable());
				columns.add(column);
			}else if (expression instanceof Function){
				Function function = (Function) expression;
				column.setFunction(function);
				columns.add(column);
				
			}
		}else{
			LOG.warn("Expression Object is Null for the SelectExpressionItem [{}] ",selectExpressionItem);
		}
		columns.add(column);
	}

}
