package org.reporte.report.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrossTabReport extends BaseReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<CrossTabAttribute> attributes;
	private List<Map<String, String>> resultSet;

	public List<CrossTabAttribute> getAttributes() {
		if (attributes==null) {
			attributes = new ArrayList<CrossTabAttribute>();
		}
		return attributes;
	}

	public void setAttributes(List<CrossTabAttribute> attributes) {
		this.attributes = attributes;
	}

	public List<Map<String, String>> getResultSet() {
		if (resultSet==null) {
			resultSet = new ArrayList<Map<String,String>>();
		}
		return resultSet;
	}

	public void setResultSet(List<Map<String, String>> resultSet) {
		this.resultSet = resultSet;
	}

}
