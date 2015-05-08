package org.reportbay.snapshot.service.exception;

import javax.ejb.ApplicationException;

//roll back any transaction
@ApplicationException(rollback=true)
public class SnapShotServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public SnapShotServiceException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public SnapShotServiceException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public SnapShotServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}