package org.reportbay.api.service.exception;

public class ReportConnectorServiceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public ReportConnectorServiceException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public ReportConnectorServiceException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public ReportConnectorServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}