package org.reportbay.reporttemplate.service.impl;

public enum ExpressionType {
	Addition, AllComparisonExpression, AndExpression, AnyComparisonExpression, 
	Between, BinaryExpression, CaseExpression, Column, DateValue, Division, 
	DoubleValue, EqualsTo, ExistsExpression, Function, GreaterThan, GreaterThanEquals, 
	InExpression, InverseExpression, IsNullExpression, JdbcParameter, LikeExpression, 
	LongValue, MinorThan, MinorThanEquals, Multiplication, NotEqualsTo, NullValue, 
	OrExpression, Parenthesis, StringValue, SubSelect, Subtraction, TimestampValue, TimeValue, WhenClause;
}
