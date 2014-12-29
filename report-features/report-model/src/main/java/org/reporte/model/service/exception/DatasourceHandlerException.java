package org.reporte.model.service.exception;

import javax.ejb.ApplicationException;

@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class DatasourceHandlerException extends Exception {
	
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
