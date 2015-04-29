package org.reportbay.report.domain;



public class ChartReport extends BaseReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String title;
	/**
	 * 
	 */
	private boolean showLegend;
	/**
	 * 
	 * @return 
	 */
	public String getTitle() {
	 	 return title; 
	}
	/**
	 * 
	 * @param title 
	 */
	public void setTitle(String title) { 
		 this.title = title; 
	}
	/**
	 * Getter of showLegend
	 */
	public boolean getShowLegend() {
	 	 return showLegend; 
	}
	/**
	 * 
	 * @param showLegend 
	 */
	public void setShowLegend(boolean showLegend) { 
		 this.showLegend = showLegend; 
	}
	/**
	 * 
	 * @return 
	 */
	public boolean isShowLegend() { 
		return showLegend;
	 } 

}
