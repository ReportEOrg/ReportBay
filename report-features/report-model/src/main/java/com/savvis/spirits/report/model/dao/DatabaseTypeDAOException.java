package com.savvis.spirits.report.model.dao;

import javax.ejb.ApplicationException;

@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class DatabaseTypeDAOException extends Exception {

	public DatabaseTypeDAOException(String message) {
		super(message);
	}

	public DatabaseTypeDAOException(Throwable cause) {
		super(cause);
	}

	public DatabaseTypeDAOException(String message, Throwable cause) {
		super(message, cause);
	}
}
