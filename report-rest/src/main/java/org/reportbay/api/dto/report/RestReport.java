package org.reportbay.api.dto.report;

import java.io.Serializable;

import org.reportbay.report.domain.CartesianChartReport;
import org.reportbay.report.domain.CrossTabReport;
import org.reportbay.report.domain.PieChartReport;


/**
 * 
 * JSON Wrapper class for holding report 
 * 
 * TODO: check alternative to return different report type
 *
 */
public class RestReport implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String type;
	
	private CartesianChartReport cartesianChartReport;
	private PieChartReport pieChartReport;
	private CrossTabReport crossTabReport;
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the cartesianChartReport
	 */
	public CartesianChartReport getCartesianChartReport() {
		return cartesianChartReport;
	}
	/**
	 * @param cartesianChartReport the cartesianChartReport to set
	 */
	public void setCartesianChartReport(CartesianChartReport cartesianChartReport) {
		this.cartesianChartReport = cartesianChartReport;
	}
	/**
	 * @return the pieChartReport
	 */
	public PieChartReport getPieChartReport() {
		return pieChartReport;
	}
	/**
	 * @param pieChartReport the pieChartReport to set
	 */
	public void setPieChartReport(PieChartReport pieChartReport) {
		this.pieChartReport = pieChartReport;
	}
	public CrossTabReport getCrossTabReport() {
		return crossTabReport;
	}
	public void setCrossTabReport(CrossTabReport crossTabReport) {
		this.crossTabReport = crossTabReport;
	}
}