/**
 * 
 */
package org.reportbay.reporttemplate.domain;

/**
 * @author ahamed.nijamudeen
 * @version 1.0
 * @since 01-Feb-2015
 */
public enum SqlFunction {

	GROUPBY("groupby"),AVG("avg"),COUNT("count"),FIRST("first"),LAST("last"),MAX("max"),
	MIN("min"),SUM("sum");
	
	private String value;
	
	private SqlFunction(String value){
		this.value=value;
	}
	
	public String getValue() {
		return this.value;
	}
}
