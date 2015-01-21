package org.reporte.model.dao.exception;

import javax.ejb.ApplicationException;

import org.reporte.common.dao.exception.BaseDAOException;

@ApplicationException(rollback=true)
public class ModelDAOException extends BaseDAOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public ModelDAOException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public ModelDAOException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public ModelDAOException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
