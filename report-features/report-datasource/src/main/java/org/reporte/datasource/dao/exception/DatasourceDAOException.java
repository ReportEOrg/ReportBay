package org.reporte.datasource.dao.exception;

import javax.ejb.ApplicationException;

import org.reporte.common.dao.exception.BaseDAOException;

@ApplicationException(rollback=true)
public class DatasourceDAOException extends BaseDAOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
