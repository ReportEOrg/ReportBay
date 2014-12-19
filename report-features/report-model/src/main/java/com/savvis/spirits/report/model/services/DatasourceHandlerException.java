package com.savvis.spirits.report.model.services;

import javax.ejb.ApplicationException;

@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class DatasourceHandlerException extends Exception {
	
	public DatasourceHandlerException(String message) {
		super(message);
	}

	public DatasourceHandlerException(Throwable cause) {
		super(cause);
	}

	public DatasourceHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

}
