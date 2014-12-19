package com.savvis.spirits.report.model.dao;

import javax.ejb.ApplicationException;

@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class ModelDAOException extends Exception {
	
	public ModelDAOException(String message) {
		super(message);
	}
	
	public ModelDAOException(Throwable cause) {
		super(cause);
	}

	public ModelDAOException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
