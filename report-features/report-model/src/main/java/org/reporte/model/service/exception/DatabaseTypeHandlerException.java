package org.reporte.model.service.exception;

import javax.ejb.ApplicationException;

@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class DatabaseTypeHandlerException extends Exception {

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
