package org.reporte.report.domain;

import java.util.List;

public class CartesianChartReport extends ChartReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String xAxisTitle;
	/**
	 * 
	 */
	private String yAxisTitle;
	/**
	 * 
	 */
	private boolean showXAxis;
	/**
	 * 
	 */
	private boolean showYAxis;
	/**
	 * 
	 */
	private boolean showDataLabel;
	/**
	 * 
	 */
	private List<ChartSeries> chartDataSeries;
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
	 * Getter of showXAxis
	 */
	public boolean getShowXAxis() {
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
	 * Getter of showYAxis
	 */
	public boolean getShowYAxis() {
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
	public List<ChartSeries> getChartDataSeries() {
	 	 return chartDataSeries; 
	}
	/**
	 * Setter of chartDataSeries
	 */
	public void setChartDataSeries(List<ChartSeries> chartDataSeries) { 
		 this.chartDataSeries = chartDataSeries; 
	}
	/**
	 * 
	 * @return 
	 */
	public boolean isShowXAxis() { 
		return showXAxis;
	 }
	/**
	 * 
	 * @return 
	 */
	public boolean isShowYAxis() { 
		return showYAxis;
	 }
	/**
	 * 
	 * @return 
	 */
	public boolean isShowDataLabel() { 
		return showDataLabel;
	 }
}
