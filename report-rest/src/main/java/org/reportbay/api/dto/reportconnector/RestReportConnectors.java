package org.reportbay.api.dto.reportconnector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RestReportConnectors implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<RestLiteReportConnector> connectors = new ArrayList<RestLiteReportConnector>();

	/**
	 * @return the connectors
	 */
	public List<RestLiteReportConnector> getConnectors() {
		return connectors;
	}

	/**
	 * @param connectors the connectors to set
	 */
	public void setConnectors(List<RestLiteReportConnector> connectors) {
		this.connectors = connectors;
	}
	
}