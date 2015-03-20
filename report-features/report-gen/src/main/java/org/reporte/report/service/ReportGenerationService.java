package org.reporte.report.service;

import java.util.List;
import java.util.Optional;

import org.reporte.datasource.domain.Datasource;
import org.reporte.model.domain.Model;
import org.reporte.report.domain.AreaChartReport;
import org.reporte.report.domain.BarChartReport;
import org.reporte.report.domain.ColumnChartReport;
import org.reporte.report.domain.CrossTabReport;
import org.reporte.report.domain.LineChartReport;
import org.reporte.report.domain.PieChartReport;
import org.reporte.report.service.exception.ReportGenerationServiceException;
import org.reporte.reporttemplate.domain.AreaChartTemplate;
import org.reporte.reporttemplate.domain.BarChartTemplate;
import org.reporte.reporttemplate.domain.ColumnChartTemplate;
import org.reporte.reporttemplate.domain.CrossTabTemplate;
import org.reporte.reporttemplate.domain.LineChartTemplate;
import org.reporte.reporttemplate.domain.PieChartTemplate;
import org.reporte.reporttemplate.domain.ReportQuery;
import org.reporte.reporttemplate.service.exception.ReportTemplateServiceException;

/**
 * 
 * Report Generation Service Interface
 *
 */
public interface ReportGenerationService {

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	AreaChartReport generateAreaChartReport(int reportTemplateId) throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	AreaChartReport generateAreaChartReport(AreaChartTemplate reportTemplate) throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	BarChartReport generateBarChartReport(int reportTemplateId) throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	BarChartReport generateBarChartReport(BarChartTemplate reportTemplate) throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	ColumnChartReport generateColumnChartReport(int reportTemplateId) throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	ColumnChartReport generateColumnChartReport(ColumnChartTemplate reportTemplate) throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	LineChartReport generateLineChartReport(int reportTemplateId) throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	LineChartReport generateLineChartReport(LineChartTemplate reportTemplate) throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	PieChartReport generatePieChartReport(int reportTemplateId) throws ReportGenerationServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	PieChartReport generatePieChartReport(PieChartTemplate reportTemplate) throws ReportGenerationServiceException;
	
	/**
	 * Return CrossTabReport object containing attributes value and its display details 
	 * @param reportTemplateId
	 * @return {@link Optional<{@link CrossTabReport}>
	 * @throws ReportGenerationServiceException
	 */
	Optional<CrossTabReport> generateCrossTabReport(int reportTemplateId) throws ReportGenerationServiceException;
	
	/**
	 * Return CrossTabReport object containing attributes value and its display details
	 * @param reportTemplate
	 * @return {@link Optional<{@link CrossTabReport}>
	 * @throws ReportGenerationServiceException
	 */
	Optional<CrossTabReport> generateCrossTabReport(CrossTabTemplate reportTemplate) throws ReportGenerationServiceException;

	/**
	 * 
	 * @param dataSource
	 * @param query
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	List<String> getDataFieldValues(Datasource dataSource,String query) throws ReportGenerationServiceException;

}