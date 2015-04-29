/**
 * 
 */
package org.reportbay.reporttemplate.domain;

/**
 * @author ahamed.nijamudeen
 *
 */
public enum GroupOrAggregate {
	
	GROUPING("grouping"),AGGREGATE("aggregate");
	
	private String value;
	
	private GroupOrAggregate(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}

}
