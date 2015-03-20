/**
 * 
 */
package org.reporte.common.domain;

/**
 * @author ahamed.nijamudeen
 *
 */
public enum OrderBy {

	DESC("desc"),ASC("asc");
	
	private String order;
	
	private OrderBy(String order){
		this.order=order;
	}
	
	public String getOrder(){
		return order;
	}
}
