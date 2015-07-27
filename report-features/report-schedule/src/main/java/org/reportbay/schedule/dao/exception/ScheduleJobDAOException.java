package org.reportbay.schedule.dao.exception;

import javax.ejb.ApplicationException;

import org.reportbay.common.dao.exception.BaseDAOException;

@ApplicationException(rollback=true)
public class ScheduleJobDAOException extends BaseDAOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public ScheduleJobDAOException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public ScheduleJobDAOException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public ScheduleJobDAOException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
