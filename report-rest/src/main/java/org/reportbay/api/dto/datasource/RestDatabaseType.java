package org.reportbay.api.dto.datasource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.reportbay.datasource.domain.DatabaseType;

public class RestDatabaseType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<DatabaseType> types = new ArrayList<DatabaseType>();

	/**
	 * 
	 * @return
	 */
	public List<DatabaseType> getTypes() {
		return types;
	}

	/**
	 * 
	 * @param databaseTypes
	 */
	public void setTypes(List<DatabaseType> types) {
		this.types = types;
	}
	
}
