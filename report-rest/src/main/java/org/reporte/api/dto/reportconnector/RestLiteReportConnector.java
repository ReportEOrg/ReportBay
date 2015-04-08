package org.reporte.api.dto.reportconnector;

import java.io.Serializable;

public class RestLiteReportConnector implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String reportDisplayName;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the reportDisplayName
	 */
	public String getReportDisplayName() {
		return reportDisplayName;
	}
	/**
	 * @param reportDisplayName the reportDisplayName to set
	 */
	public void setReportDisplayName(String reportDisplayName) {
		this.reportDisplayName = reportDisplayName;
	}
}