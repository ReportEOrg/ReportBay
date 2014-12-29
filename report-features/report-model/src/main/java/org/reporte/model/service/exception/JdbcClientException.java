package org.reporte.model.service.exception;

@SuppressWarnings("serial")
public class JdbcClientException extends Exception {

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
