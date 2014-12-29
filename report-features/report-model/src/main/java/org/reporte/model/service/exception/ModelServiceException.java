package org.reporte.model.service.exception;

import javax.ejb.ApplicationException;

@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class ModelServiceException extends Exception {
	/**
	 * 
	 * @param message
	 */
	public ModelServiceException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public ModelServiceException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public ModelServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
