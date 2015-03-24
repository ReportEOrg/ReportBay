package org.reporte.api.dto.reportconnector;

import java.io.Serializable;

import org.reporte.reporttemplate.domain.CrossTabTemplate;
import org.reporte.reporttemplate.domain.CartesianChartTemplate;
import org.reporte.reporttemplate.domain.PieChartTemplate;


public class RestReportConnector implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TemplateType type;

	private CartesianChartTemplate cartesianChartTemplate;
	
	private PieChartTemplate pieChartTemplate;

	/** CrossTab Template ***/
	private CrossTabTemplate crossTabTemplate;
	

	public CrossTabTemplate getCrossTabTemplate() {
		return crossTabTemplate;
	}

	public void setCrossTabTemplate(CrossTabTemplate crossTabTemplate) {
		this.crossTabTemplate = crossTabTemplate;
	}
	/**
	 * @return the type
	 */
	public TemplateType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TemplateType type) {
		this.type = type;
	}

	/**
	 * @return the cartesianChartTemplate
	 */
	public CartesianChartTemplate getCartesianChartTemplate() {
		return cartesianChartTemplate;
	}

	/**
	 * @param cartesianChartTemplate the cartesianChartTemplate to set
	 */
	public void setCartesianChartTemplate(CartesianChartTemplate cartesianChartTemplate) {
		this.cartesianChartTemplate = cartesianChartTemplate;
	}

	/**
	 * @return the pieChartTemplate
	 */
	public PieChartTemplate getPieChartTemplate() {
		return pieChartTemplate;
	}

	/**
	 * @param pieChartTemplate the pieChartTemplate to set
	 */
	public void setPieChartTemplate(PieChartTemplate pieChartTemplate) {
		this.pieChartTemplate = pieChartTemplate;
	}
}