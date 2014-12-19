package com.savvis.spirits.report.model.dao;

import javax.ejb.ApplicationException;

@SuppressWarnings("serial")
@ApplicationException(rollback=true)
public class DatasourceDAOException extends Exception {

	public DatasourceDAOException(String message) {
		super(message);
	}

	public DatasourceDAOException(Throwable cause) {
		super(cause);
	}

	public DatasourceDAOException(String message, Throwable cause) {
		super(message, cause);
	}

}
