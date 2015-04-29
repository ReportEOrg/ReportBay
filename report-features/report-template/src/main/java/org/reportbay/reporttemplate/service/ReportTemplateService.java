package org.reportbay.reporttemplate.service;

import java.util.List;
import java.util.Optional;

import net.sf.jsqlparser.statement.select.Select;

import org.reportbay.reporttemplate.domain.AreaChartTemplate;
import org.reportbay.reporttemplate.domain.BarChartTemplate;
import org.reportbay.reporttemplate.domain.BaseReportTemplate;
import org.reportbay.reporttemplate.domain.ColumnChartTemplate;
import org.reportbay.reporttemplate.domain.CrossTabTemplate;
import org.reportbay.reporttemplate.domain.LineChartTemplate;
import org.reportbay.reporttemplate.domain.PieChartTemplate;
import org.reportbay.reporttemplate.domain.ReportQuery;
import org.reportbay.reporttemplate.service.exception.ReportTemplateServiceException;

public interface ReportTemplateService {

	/****************************
	 * limit the save/update/delete type to specific child class
	 ****************************/

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	AreaChartTemplate save(AreaChartTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return the updated managed entity for subsequence usage
	 * @throws ReportTemplateServiceException
	 */
	AreaChartTemplate update(AreaChartTemplate reportTemplate) throws ReportTemplateServiceException;
	
	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	BaseReportTemplate findReportTemplate(int reportTemplateId) throws ReportTemplateServiceException;
	/**
	 * 
	 * @param reportTemplate
	 * @throws ReportTemplateServiceException
	 */
	void delete(AreaChartTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	AreaChartTemplate findAreaChartTemplate(int reportTemplateId) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	BarChartTemplate save(BarChartTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return the updated managed entity for subsequence usage
	 * @throws ReportTemplateServiceException
	 */
	BarChartTemplate update(BarChartTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @throws ReportTemplateServiceException
	 */
	void delete(BarChartTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	BarChartTemplate findBarChartTemplate(int reportTemplateId) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	ColumnChartTemplate save(ColumnChartTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return the updated managed entity for subsequence usage
	 * @throws ReportTemplateServiceException
	 */
	ColumnChartTemplate update(ColumnChartTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @throws ReportTemplateServiceException
	 */
	void delete(ColumnChartTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	ColumnChartTemplate findColumnChartTemplate(int reportTemplateId) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	LineChartTemplate save(LineChartTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return the updated managed entity for subsequence usage
	 * @throws ReportTemplateServiceException
	 */
	LineChartTemplate update(LineChartTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @throws ReportTemplateServiceException
	 */
	void delete(LineChartTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	LineChartTemplate findLineChartTemplate(int reportTemplateId) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	PieChartTemplate save(PieChartTemplate reportTemplate) throws ReportTemplateServiceException;
	
	/**
	 * Save newly created CrossTab template into database and return the saved template object back to the 
	 * caller.
	 * 
	 * @param  crossTabTemplate pass {@link CrossTabTemplate} object to save into database 
	 * @return {@link Optional}<{@link CrossTabTemplate}>
	 * @throws ReportTemplateServiceException
	 */
	Optional<CrossTabTemplate> save(CrossTabTemplate crossTabTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @return the updated managed entity for subsequence usage
	 * @throws ReportTemplateServiceException
	 */
	PieChartTemplate update(PieChartTemplate reportTemplate) throws ReportTemplateServiceException;
	
	/**
	 * update CrossTab Template
	 * @param reportTemplate
	 * @return {@link Optional}<{@link CrossTabTemplate}>
	 * @throws ReportTemplateServiceException
	 */
	Optional<CrossTabTemplate> update(CrossTabTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplate
	 * @throws ReportTemplateServiceException
	 */
	void delete(PieChartTemplate reportTemplate) throws ReportTemplateServiceException;
	
	/**
	 * Delete CrossTab Template
	 * @param reportTemplate
	 * @throws ReportTemplateServiceException
	 */
	void delete(CrossTabTemplate reportTemplate) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	PieChartTemplate findPieChartTemplate(int reportTemplateId) throws ReportTemplateServiceException;
	/**
	 * Find CrossTab Template by passing template ID
	 * @param reportTemplateId
	 * @return {@link Optional}<{@link CrossTabTemplate}>
	 * @throws ReportTemplateServiceException
	 */
	Optional<CrossTabTemplate> findCrossTabTemplate(int reportTemplateId) throws ReportTemplateServiceException;

	/**
	 * 
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	List<BaseReportTemplate> findAllReportTemplate() throws ReportTemplateServiceException;

	/**
	 * 
	 * @param template
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	ReportQuery constructReportQuery(AreaChartTemplate template) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param template
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	ReportQuery constructReportQuery(BarChartTemplate template) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param template
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	ReportQuery constructReportQuery(ColumnChartTemplate template) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param template
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	ReportQuery constructReportQuery(LineChartTemplate template) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param template
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	ReportQuery constructReportQuery(PieChartTemplate template) throws ReportTemplateServiceException;

	/**
	 * 
	 * @param templateId
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	ReportQuery findReportQuery(int templateId) throws ReportTemplateServiceException;
	
	/**
	 * Generate ReportQuery for the given CrossTabTemplate designed by User
	 * @param crossTabTemplate
	 * @return {@link Optional}<{@link ReportQuery}>
	 * @throws ReportTemplateServiceException
	 */
	Optional<ReportQuery> constructReportQuery(CrossTabTemplate crossTabTemplate) throws ReportTemplateServiceException;
	/**
	 * Parse the Select Query String and return {@link Select} object.
	 * @param {@link String} String representation of Query String
	 * @return {@link Optional}<{@link Select}> return a optional select object
	 * @throws throws exception when the Query is not of type select or Invalid SQL query.
	 */
	Optional<Select> parseSelectQuery(String query) throws ReportTemplateServiceException;
	/**
	 * 
	 * @param reportTemplate
	 * @throws ReportTemplateServiceException
	 */
	void deleteReportTemplate(BaseReportTemplate reportTemplate) throws ReportTemplateServiceException ;
}