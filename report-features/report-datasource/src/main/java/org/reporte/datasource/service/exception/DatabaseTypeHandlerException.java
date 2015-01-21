package org.reporte.datasource.service.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class DatabaseTypeHandlerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public DatabaseTypeHandlerException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public DatabaseTypeHandlerException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public DatabaseTypeHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

}
