package org.reportbay.publish.service.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class PublishServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public PublishServiceException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public PublishServiceException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public PublishServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}