package org.reportbay.snapshot.dao.exception;

import javax.ejb.ApplicationException;

import org.reportbay.common.dao.exception.BaseDAOException;

@ApplicationException(rollback=true)
public class SnapShotDAOException extends BaseDAOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public SnapShotDAOException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public SnapShotDAOException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public SnapShotDAOException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
