package org.reporte.reporttemplate.dao.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class ReportTemplateDAOException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public ReportTemplateDAOException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public ReportTemplateDAOException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public ReportTemplateDAOException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
