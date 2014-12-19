package com.savvis.spirits.report.model.services;

import javax.ejb.ApplicationException;

@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class ModelServiceException extends Exception {
	
	public ModelServiceException(String message) {
		super(message);
	}

	public ModelServiceException(Throwable cause) {
		super(cause);
	}

	public ModelServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
