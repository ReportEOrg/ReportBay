package org.reporte.model.service.exception;

public class JdbcClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param message
	 */
	public JdbcClientException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public JdbcClientException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public JdbcClientException(String message, Throwable cause) {
		super(message, cause);
	}

}
