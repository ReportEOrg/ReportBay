package org.reportbay.reporttemplate.domain;

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
	
	/**
	 * 
	 */
	@Column(name="data_label_ind")
	private boolean showDataLabel;
	/**
	 * 
	 */
	@Column(name="model_data_label_field")
	private String modelDataLabelField;
	/**
	 * 
	 */
	@Column(name="model_data_value_field")
	private String modelDataValueField;
	/**
	 * 
	 */
	@Column(name="model_series_grp_field")
	private String modelSeriesGroupField;
	
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
		  .append(modelDataValueField, testRef.modelDataValueField)
		  .append(modelSeriesGroupField, testRef.modelSeriesGroupField)
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
		   .append(modelDataValueField)
		   .append(modelSeriesGroupField)
		   .append(getDataSeries());
		
		return hcb.toHashCode();
	}
	/**
	 * @return the showDataLabel
	 */
	public boolean isShowDataLabel() {
		return showDataLabel;
	}
	/**
	 * @param showDataLabel the showDataLabel to set
	 */
	public void setShowDataLabel(boolean showDataLabel) {
		this.showDataLabel = showDataLabel;
	}
	/**
	 * @return
	 */
	public boolean getShowDataLabel() {
		return showDataLabel;
	}
	/**
	 * @return the modelDataValueField
	 */
	public String getModelDataValueField() {
		return modelDataValueField;
	}
	/**
	 * @param modelDataValueField the modelDataValueField to set
	 */
	public void setModelDataValueField(String modelDataValueField) {
		this.modelDataValueField = modelDataValueField;
	}
	/**
	 * @return the modelSeriesGroupField
	 */
	public String getModelSeriesGroupField() {
		return modelSeriesGroupField;
	}
	/**
	 * @param modelSeriesGroupField the modelSeriesGroupField to set
	 */
	public void setModelSeriesGroupField(String modelSeriesGroupField) {
		this.modelSeriesGroupField = modelSeriesGroupField;
	}
}
