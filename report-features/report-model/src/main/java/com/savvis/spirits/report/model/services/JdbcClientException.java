package com.savvis.spirits.report.model.services;

@SuppressWarnings("serial")
public class JdbcClientException extends Exception {

	public JdbcClientException(String message) {
		super(message);
	}

	public JdbcClientException(Throwable cause) {
		super(cause);
	}

	public JdbcClientException(String message, Throwable cause) {
		super(message, cause);
	}

}
