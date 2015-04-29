package org.reportbay.api.dto.schedule;

import java.io.Serializable;

public class Schedule implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String year;
	private String month;
	private String dayOfWeek;
	private String dayOfMonth;
	private String hour;
	private String minute;
	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}
	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}
	/**
	 * @param month the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
	}
	/**
	 * @return the dayOfWeek
	 */
	public String getDayOfWeek() {
		return dayOfWeek;
	}
	/**
	 * @param dayOfWeek the dayOfWeek to set
	 */
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	/**
	 * @return the dayOfMonth
	 */
	public String getDayOfMonth() {
		return dayOfMonth;
	}
	/**
	 * @param dayOfMonth the dayOfMonth to set
	 */
	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	/**
	 * @return the hour
	 */
	public String getHour() {
		return hour;
	}
	/**
	 * @param hour the hour to set
	 */
	public void setHour(String hour) {
		this.hour = hour;
	}
	/**
	 * @return the minute
	 */
	public String getMinute() {
		return minute;
	}
	/**
	 * @param minute the minute to set
	 */
	public void setMinute(String minute) {
		this.minute = minute;
	}
}