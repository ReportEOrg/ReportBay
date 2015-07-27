package org.reportbay.schedule.service.exception;

import javax.ejb.ApplicationException;

//roll back any transaction
@ApplicationException(rollback=true)
public class ScheduleServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public ScheduleServiceException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public ScheduleServiceException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public ScheduleServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}