package org.reportbay.common.dao.exception;

/**
 * 
 * Base DAO Exception for 
 * 1) finer classification of DAO nature exception
 * 2) simplified catch implementation in java 6 where multiple type exception catch not yet supported 
 */
public class BaseDAOException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param message
	 */
	public BaseDAOException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public BaseDAOException(Throwable cause) {
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public BaseDAOException(String message, Throwable cause) {
		super(message, cause);
	}
}