package org.reportbay.api.dto.schedule;

import java.io.Serializable;

public class ScheduleTask implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private int reportConnectorId;
	private String callbackUrl;
	private Schedule schedule;
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
	 * @return the reportConnectorId
	 */
	public int getReportConnectorId() {
		return reportConnectorId;
	}
	/**
	 * @param reportConnectorId the reportConnectorId to set
	 */
	public void setReportConnectorId(int reportConnectorId) {
		this.reportConnectorId = reportConnectorId;
	}
	/**
	 * @return the callbackUrl
	 */
	public String getCallbackUrl() {
		return callbackUrl;
	}
	/**
	 * @param callbackUrl the callbackUrl to set
	 */
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	/**
	 * @return the schedule
	 */
	public Schedule getSchedule() {
		return schedule;
	}
	/**
	 * @param schedule the schedule to set
	 */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
}