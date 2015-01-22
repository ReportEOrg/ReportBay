package org.reporte.report.domain;

import org.reporte.reporttemplate.domain.PieChartDataTypeEnum;

import java.util.Map;

public class PieChartReport extends ChartReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private boolean showDataLabel;
	/**
	 * 
	 */
	private PieChartDataTypeEnum dataTypeFormat;
	/**
	 * 
	 */
	private Map<String,Number> categoryData;
	/**
	 * Getter of showDataLabel
	 */
	public boolean getShowDataLabel() {
	 	 return showDataLabel; 
	}
	/**
	 * 
	 * @param showDataLabel 
	 */
	public void setShowDataLabel(boolean showDataLabel) { 
		 this.showDataLabel = showDataLabel; 
	}
	/**
	 * 
	 * @return 
	 */
	public PieChartDataTypeEnum getDataTypeFormat() {
	 	 return dataTypeFormat; 
	}
	/**
	 * 
	 * @param dataTypeFormat 
	 */
	public void setDataTypeFormat(PieChartDataTypeEnum dataTypeFormat) { 
		 this.dataTypeFormat = dataTypeFormat; 
	}
	/**
	 * 
	 * @return 
	 */
	public Map<String,Number> getCategoryData() {
	 	 return categoryData; 
	}
	/**
	 * 
	 * @param categoryData 
	 */
	public void setCategoryData(Map<String,Number> categoryData) { 
		 this.categoryData = categoryData; 
	}
	/**
	 * 
	 * @return 
	 */
	public boolean isShowDataLabel() { 
		return showDataLabel;
	 } 

}
