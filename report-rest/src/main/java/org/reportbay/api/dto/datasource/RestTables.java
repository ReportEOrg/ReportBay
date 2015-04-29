package org.reportbay.api.dto.datasource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * Wrapper class for JSON pojo
 */
public class RestTables implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<RestTable> tables = new ArrayList<RestTable>();

	/**
	 * @return the tables
	 */
	public List<RestTable> getTables() {
		return tables;
	}

	/**
	 * @param tables the tables to set
	 */
	public void setTables(List<RestTable> tables) {
		this.tables = tables;
	}
}