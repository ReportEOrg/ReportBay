package org.reporte.api.dto.schedule;

import java.io.Serializable;
import java.util.List;

public class ScheduleTasks implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ScheduleTask> scheduleTasks;

	/**
	 * @return the scheduleTasks
	 */
	public List<ScheduleTask> getScheduleTasks() {
		return scheduleTasks;
	}

	/**
	 * @param scheduleTasks the scheduleTasks to set
	 */
	public void setScheduleTasks(List<ScheduleTask> scheduleTasks) {
		this.scheduleTasks = scheduleTasks;
	}
}