package org.reporte.datasource.dao.exception;

import javax.ejb.ApplicationException;

import org.reporte.common.dao.exception.BaseDAOException;

@ApplicationException(rollback=true)
public class DatabaseTypeDAOException extends BaseDAOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public DatabaseTypeDAOException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public DatabaseTypeDAOException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public DatabaseTypeDAOException(String message, Throwable cause) {
		super(message, cause);
	}
}
