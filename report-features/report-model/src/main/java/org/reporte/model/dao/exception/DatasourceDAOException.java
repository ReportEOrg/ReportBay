package org.reporte.model.dao.exception;

import javax.ejb.ApplicationException;

@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class DatasourceDAOException extends Exception {
	/**
	 * 
	 * @param message
	 */
	public DatasourceDAOException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public DatasourceDAOException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public DatasourceDAOException(String message, Throwable cause) {
		super(message, cause);
	}

}