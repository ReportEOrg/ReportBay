package org.reporte.api.dto.datasource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.reporte.datasource.domain.Datasource;

/***
 * Wrapper class for JSON pojo
 */
public class RestDataSources implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Datasource> datasources = new ArrayList<Datasource>();

	/**
	 * 
	 * @return
	 */
	public List<Datasource> getDatasources() {
		return datasources;
	}

	/**
	 * 
	 * @param datasources
	 */
	public void setDatasources(List<Datasource> datasources) {
		this.datasources = datasources;
	}
	
}