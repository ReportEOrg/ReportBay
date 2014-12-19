package com.savvis.spirits.report.model.services;

import javax.ejb.ApplicationException;

@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class DatabaseTypeHandlerException extends Exception {

	public DatabaseTypeHandlerException(String message) {
		super(message);
	}

	public DatabaseTypeHandlerException(Throwable cause) {
		super(cause);
	}

	public DatabaseTypeHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

}
