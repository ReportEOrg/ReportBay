package org.reporte.report.domain;

import java.io.Serializable;

import org.reporte.reporttemplate.domain.ReportTemplateTypeEnum;

public abstract class BaseReport implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String reportName;
	/**
	 * 
	 */
	private ReportTemplateTypeEnum reportType;
	/**
	 * 
	 * @return 
	 */
	public String getReportName() {
	 	 return reportName; 
	}
	/**
	 * 
	 * @param reportName 
	 */
	public void setReportName(String reportName) { 
		 this.reportName = reportName; 
	}
	/**
	 * 
	 * @return 
	 */
	public ReportTemplateTypeEnum getReportType() {
	 	 return reportType; 
	}
	/**
	 * 
	 * @param reportType 
	 */
	public void setReportType(ReportTemplateTypeEnum reportType) { 
		 this.reportType = reportType; 
	} 

}
