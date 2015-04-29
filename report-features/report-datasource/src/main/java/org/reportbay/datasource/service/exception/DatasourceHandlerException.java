package org.reportbay.datasource.service.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class DatasourceHandlerException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param message
	 */
	public DatasourceHandlerException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public DatasourceHandlerException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public DatasourceHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

}
