package org.reportbay.report.service;

import java.util.List;
import java.util.Optional;

import org.reportbay.datasource.domain.Datasource;
import org.reportbay.report.domain.AreaChartReport;
import org.reportbay.report.domain.BarChartReport;
import org.reportbay.report.domain.ColumnChartReport;
import org.reportbay.report.domain.CrossTabReport;
import org.reportbay.report.domain.LineChartReport;
import org.reportbay.report.domain.PieChartReport;
import org.reportbay.report.service.exception.ReportGenerationServiceException;
import org.reportbay.reporttemplate.domain.AreaChartTemplate;
import org.reportbay.reporttemplate.domain.BarChartTemplate;
import org.reportbay.reporttemplate.domain.ColumnChartTemplate;
import org.reportbay.reporttemplate.domain.CrossTabTemplate;
import org.reportbay.reporttemplate.domain.LineChartTemplate;
import org.reportbay.reporttemplate.domain.PieChartTemplate;

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