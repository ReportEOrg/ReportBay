package org.reporte.reporttemplate.service;

import java.util.List;

import org.reporte.reporttemplate.domain.AreaChartTemplate;
import org.reporte.reporttemplate.domain.BarChartTemplate;
import org.reporte.reporttemplate.domain.BaseReportTemplate;
import org.reporte.reporttemplate.domain.ColumnChartTemplate;
import org.reporte.reporttemplate.domain.LineChartTemplate;
import org.reporte.reporttemplate.domain.PieChartTemplate;
import org.reporte.reporttemplate.domain.ReportQuery;
import org.reporte.reporttemplate.service.exception.ReportTemplateServiceException;

public interface ReportTemplateService{
	
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
	 * 
	 * @param reportTemplate
	 * @return the updated managed entity for subsequence usage
	 * @throws ReportTemplateServiceException
	 */
	PieChartTemplate update(PieChartTemplate reportTemplate) throws ReportTemplateServiceException;
	
	/**
	 * 
	 * @param reportTemplate
	 * @throws ReportTemplateServiceException
	 */
	void delete(PieChartTemplate reportTemplate) throws ReportTemplateServiceException;
	
	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	PieChartTemplate findPieChartTemplate(int reportTemplateId) throws ReportTemplateServiceException;
	
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
}