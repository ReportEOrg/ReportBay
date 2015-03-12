package org.reporte.api.dto.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * Wrapper class for JSON pojo
 */
public class RestModelPreviewResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int matchRecordCount;
	
	private List<String> columnNameList = new ArrayList<String>();
	
	private List<Map<String, String>> resultRowList = new ArrayList<Map<String, String>>();
	/**
	 * @return the matchRecordCount
	 */
	public int getMatchRecordCount() {
		return matchRecordCount;
	}

	/**
	 * @param matchRecordCount the matchRecordCount to set
	 */
	public void setMatchRecordCount(int matchRecordCount) {
		this.matchRecordCount = matchRecordCount;
	}

	/**
	 * @return the resultRowList
	 */
	public List<Map<String, String>> getResultRowList() {
		return resultRowList;
	}

	/**
	 * @param resultRowList the resultRowList to set
	 */
	public void setResultRowList(List<Map<String, String>> resultRowList) {
		this.resultRowList = resultRowList;
	}

	/**
	 * @return the columnNameList
	 */
	public List<String> getColumnNameList() {
		return columnNameList;
	}

	/**
	 * @param columnNameList the columnNameList to set
	 */
	public void setColumnNameList(List<String> columnNameList) {
		this.columnNameList = columnNameList;
	}
	
}