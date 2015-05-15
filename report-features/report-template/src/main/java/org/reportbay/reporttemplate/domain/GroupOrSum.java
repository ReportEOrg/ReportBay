/**
 * 
 */
package org.reportbay.reporttemplate.domain;

/**
 * @author ahamed.nijamudeen
 *
 */
public enum GroupOrSum {
	
	GROUPING("grouping"),SUM("sum");
	
	private String value;
	
	private GroupOrSum(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}

}
