package org.reporte.api.dto.datasource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.reporte.datasource.domain.ColumnMetadata;

/***
 * Wrapper class for JSON pojo
 */
public class RestTable implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private List<ColumnMetadata> fieldList = new ArrayList<ColumnMetadata>();
	
	public RestTable(String tableName){
		this.name = tableName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the fieldList
	 */
	public List<ColumnMetadata> getFieldList() {
		return fieldList;
	}

	/**
	 * @param fieldList the fieldList to set
	 */
	public void setFieldList(List<ColumnMetadata> fieldList) {
		this.fieldList = fieldList;
	}
	
}