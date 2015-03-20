/**
 * 
 */
package org.reporte.reporttemplate.domain;

/**
 * @author ahamed.nijamudeen
 *
 */
public enum CrossTabFieldType {

	ROW("Row"),COLUMN("Column");

	private String type;
	
	private CrossTabFieldType(String type){
		this.type=type;
	}
	
	public String getType(){
		return this.type;
	}
	
}
