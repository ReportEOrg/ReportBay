package org.reportbay.report.service.exception;

import javax.ejb.ApplicationException;

//roll back any transaction
@ApplicationException(rollback=true)
public class ReportGenerationServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public ReportGenerationServiceException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public ReportGenerationServiceException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public ReportGenerationServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}