package org.reporte.report.service;

import org.reporte.report.domain.AreaChartReport;
import org.reporte.report.domain.BarChartReport;
import org.reporte.report.domain.ColumnChartReport;
import org.reporte.report.domain.LineChartReport;
import org.reporte.report.domain.PieChartReport;
import org.reporte.report.service.exception.ReportGenerationServiceException;

public interface ReportGenerationService {

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	AreaChartReport generateAreaChartReport(int reportTemplateId)
			throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	BarChartReport generateBarChartReport(int reportTemplateId)
			throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	ColumnChartReport generateColumnChartReport(
			int reportTemplateId) throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	LineChartReport generateLineChartReport(int reportTemplateId)
			throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	PieChartReport generatePieChartReport(int reportTemplateId)
			throws ReportGenerationServiceException;

}