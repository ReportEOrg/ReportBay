package org.reportbay.reporttemplate.dao.exception;

import javax.ejb.ApplicationException;

import org.reportbay.common.dao.exception.BaseDAOException;

@ApplicationException(rollback=true)
public class ReportQueryDAOException extends BaseDAOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public ReportQueryDAOException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public ReportQueryDAOException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public ReportQueryDAOException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
