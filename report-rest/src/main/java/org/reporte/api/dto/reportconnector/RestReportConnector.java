package org.reporte.api.dto.reportconnector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.reporte.api.dto.model.RestModel;
import org.reporte.reporttemplate.domain.CrossTabTemplate;
import org.reporte.reporttemplate.domain.TemplateSeries;

public class RestReportConnector extends RestLiteReportConnector implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String reportDisplayName;

	private RestModel model;
	
	/*** chart attributes ***/
	private String templateType;
	private String chartTitle;
	private boolean showLegend;
	
	//shared btw cartesian and pie
	private boolean showDataLabel;
	
	/*** cartesian chart attributes ***/
	private String xAxisTitle;
	private String yAxisTitle;
	private boolean showXAxis;
	private boolean showYAxis;
	private String modelDataLabelField;
	private String modelDataValueField;
	private String modelSeriesGroupField;
	private List<TemplateSeries> templateSeries = new ArrayList<TemplateSeries>();
	
	
	/*** pie chart attributes ****/
	private String categoryName;
	private String modelCategoryField;
	private String modelDataField;
	private String dataTypeFormat;

	/** CrossTab Template ***/
	private CrossTabTemplate crossTabTemplate;

	/**
	 * @return the chartType
	 */
	public String getTemplateType() {
		return templateType;
	}

	/**
	 * @param chartType the chartType to set
	 */
	public void setTemplateType(String chartType) {
		this.templateType = chartType;
	}

	/**
	 * @return the chartTitle
	 */
	public String getChartTitle() {
		return chartTitle;
	}

	/**
	 * @param chartTitle the chartTitle to set
	 */
	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	/**
	 * @return the reportDisplayName
	 */
	public String getReportDisplayName() {
		return reportDisplayName;
	}

	/**
	 * @param reportDisplayName the reportDisplayName to set
	 */
	public void setReportDisplayName(String reportDisplayName) {
		this.reportDisplayName = reportDisplayName;
	}

	/**
	 * @return the model
	 */
	public RestModel getModel() {
		return model;
	}

	/**
	 * @param model the model  to set
	 */
	public void setModel(RestModel model) {
		this.model = model;
	}

	/**
	 * @return the showLegend
	 */
	public boolean isShowLegend() {
		return showLegend;
	}

	/**
	 * @param showLegend the showLegend to set
	 */
	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	/**
	 * @return the xAxisTitle
	 */
	public String getxAxisTitle() {
		return xAxisTitle;
	}

	/**
	 * @param xAxisTitle the xAxisTitle to set
	 */
	public void setxAxisTitle(String xAxisTitle) {
		this.xAxisTitle = xAxisTitle;
	}

	/**
	 * @return the yAxisTitle
	 */
	public String getyAxisTitle() {
		return yAxisTitle;
	}

	/**
	 * @param yAxisTitle the yAxisTitle to set
	 */
	public void setyAxisTitle(String yAxisTitle) {
		this.yAxisTitle = yAxisTitle;
	}

	/**
	 * @return the showXAxis
	 */
	public boolean isShowXAxis() {
		return showXAxis;
	}

	/**
	 * @param showXAxis the showXAxis to set
	 */
	public void setShowXAxis(boolean showXAxis) {
		this.showXAxis = showXAxis;
	}

	/**
	 * @return the showYAxis
	 */
	public boolean isShowYAxis() {
		return showYAxis;
	}

	/**
	 * @param showYAxis the showYAxis to set
	 */
	public void setShowYAxis(boolean showYAxis) {
		this.showYAxis = showYAxis;
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

	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * @return the modelCategoryField
	 */
	public String getModelCategoryField() {
		return modelCategoryField;
	}

	/**
	 * @param modelCategoryField the modelCategoryField to set
	 */
	public void setModelCategoryField(String modelCategoryField) {
		this.modelCategoryField = modelCategoryField;
	}

	/**
	 * @return the modelDataField
	 */
	public String getModelDataField() {
		return modelDataField;
	}

	/**
	 * @param modelDataField the modelDataField to set
	 */
	public void setModelDataField(String modelDataField) {
		this.modelDataField = modelDataField;
	}

	/**
	 * @return the dataTypeFormat
	 */
	public String getDataTypeFormat() {
		return dataTypeFormat;
	}

	/**
	 * @param dataTypeFormat the dataTypeFormat to set
	 */
	public void setDataTypeFormat(String dataTypeFormat) {
		this.dataTypeFormat = dataTypeFormat;
	}

	/**
	 * @return the templateSeries
	 */
	public List<TemplateSeries> getTemplateSeries() {
		return templateSeries;
	}

	/**
	 * @param templateSeries the templateSeries to set
	 */
	public void setTemplateSeries(List<TemplateSeries> templateSeries) {
		this.templateSeries = templateSeries;
	}

	public CrossTabTemplate getCrossTabTemplate() {
		return crossTabTemplate;
	}

	public void setCrossTabTemplate(CrossTabTemplate crossTabTemplate) {
		this.crossTabTemplate = crossTabTemplate;
	}
}