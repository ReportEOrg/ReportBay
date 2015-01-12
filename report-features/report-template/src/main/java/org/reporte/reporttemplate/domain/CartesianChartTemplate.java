package org.reporte.reporttemplate.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 
 * JPA mapped superclass for Cartesian chart type (Bar, Column, Area, Line) template to keep common Cartesian chart attribute
 *
 */
@MappedSuperclass
public class CartesianChartTemplate extends ChartTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * chart x-axis title
	 */
	@Column(name="x_axis_title")
	private String xAxisTitle;
	/**
	 * 
	 */
	@Column(name="y_axis_title")
	private String yAxisTitle;
	/**
	 * show x-axis values
	 */
	@Column(name="x_axis_ind")
	private boolean showXAxis;
	/**
	 * 
	 */
	@Column(name="y_axis_ind")
	private boolean showYAxis;
	
	@Column(name="model_data_label_Field")
	private String modelDataLabelField;
	/**
	 * 
	 * @return 
	 */
	public String getXAxisTitle() {
	 	 return xAxisTitle; 
	}
	/**
	 * 
	 * @param xAxisTitle 
	 */
	public void setXAxisTitle(String xAxisTitle) { 
		 this.xAxisTitle = xAxisTitle; 
	}
	/**
	 * 
	 * @return 
	 */
	public String getYAxisTitle() {
	 	 return yAxisTitle; 
	}
	/**
	 * 
	 * @param yAxisTitle 
	 */
	public void setYAxisTitle(String yAxisTitle) { 
		 this.yAxisTitle = yAxisTitle; 
	}
	/**
	 * 
	 * @return
	 */
	public boolean getShowXAxis(){
		return showXAxis;
	}
	/**
	 * 
	 * @param showXAxis 
	 */
	public void setShowXAxis(boolean showXAxis) { 
		 this.showXAxis = showXAxis; 
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getShowYAxis(){
		return showYAxis;
	}
	/**
	 * 
	 * @param showYAxis 
	 */
	public void setShowYAxis(boolean showYAxis) { 
		 this.showYAxis = showYAxis; 
	}

	/**
	 * 
	 * @return 
	 */
	public boolean isShowXAxis() { 
		return this.showXAxis;
	 }
	/**
	 * 
	 * @return 
	 */
	public boolean isShowYAxis() { 
		return this.showYAxis;
	 }
	/**
	 * @return the modelDataLabelField
	 */
	public String getModelDataLabelField() {
		return modelDataLabelField;
	}
	/**
	 * @param modelDataLabelField the modelDataLabelField to set
	 */
	public void setModelDataLabelField(String modelDataLabelField) {
		this.modelDataLabelField = modelDataLabelField;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object ref){
		
		if(!super.equals(ref)){
			return false;
		}
		
		CartesianChartTemplate testRef = (CartesianChartTemplate)ref;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		eb.append(showXAxis, testRef.showXAxis)
		  .append(showYAxis, testRef.showYAxis)
		  .append(xAxisTitle, testRef.xAxisTitle)
		  .append(yAxisTitle, testRef.yAxisTitle)
		  .append(modelDataLabelField, testRef.modelDataLabelField)
		  .append(getDataSeries(), testRef.getDataSeries());
		
		return eb.isEquals();
	}		

	/**
	 * {@inheritDoc}
	 */
	public int hashCode(){
		
		HashCodeBuilder hcb = new HashCodeBuilder(INITIAL_HASH, PRIME_HASH_MULTIPLIER);
		
		hcb.appendSuper(super.hashCode())
		   .append(showXAxis)
		   .append(showYAxis)
		   .append(xAxisTitle)
		   .append(yAxisTitle)
		   .append(modelDataLabelField)
		   .append(getDataSeries());
		
		return hcb.toHashCode();
	}
}
