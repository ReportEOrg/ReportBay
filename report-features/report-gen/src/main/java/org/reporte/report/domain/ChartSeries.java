package org.reporte.report.domain;

import java.io.Serializable;
import java.util.Map;

public class ChartSeries implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String seriesName;
	/**
	 * 
	 */
	private Map<String, Number> seriesData;
	/**
	 * 
	 * @return 
	 */
	public String getSeriesName() {
	 	 return seriesName; 
	}
	/**
	 * 
	 * @param seriesName 
	 */
	public void setSeriesName(String seriesName) { 
		 this.seriesName = seriesName; 
	}
	/**
	 * 
	 * @return 
	 */
	public Map<String, Number> getSeriesData() {
	 	 return seriesData; 
	}
	/**
	 * 
	 * @param seriesData 
	 */
	public void setSeriesData(Map<String, Number> seriesData) { 
		 this.seriesData = seriesData; 
	} 

}
