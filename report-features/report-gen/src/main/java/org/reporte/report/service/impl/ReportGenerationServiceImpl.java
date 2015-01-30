package org.reporte.report.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.reporte.common.dao.exception.BaseDAOException;
import org.reporte.common.domain.SqlTypeEnum;
import org.reporte.datasource.domain.ColumnMetadata;
import org.reporte.datasource.service.JdbcClient;
import org.reporte.datasource.service.exception.JdbcClientException;
import org.reporte.report.domain.AreaChartReport;
import org.reporte.report.domain.BarChartReport;
import org.reporte.report.domain.CartesianChartReport;
import org.reporte.report.domain.ChartSeries;
import org.reporte.report.domain.ColumnChartReport;
import org.reporte.report.domain.LineChartReport;
import org.reporte.report.domain.PieChartReport;
import org.reporte.report.service.ReportGenerationService;
import org.reporte.report.service.exception.ReportGenerationServiceException;
import org.reporte.reporttemplate.dao.ReportQueryDAO;
import org.reporte.reporttemplate.dao.ReportTemplateDAO;
import org.reporte.reporttemplate.dao.exception.ReportQueryDAOException;
import org.reporte.reporttemplate.domain.AreaChartTemplate;
import org.reporte.reporttemplate.domain.BarChartTemplate;
import org.reporte.reporttemplate.domain.CartesianChartTemplate;
import org.reporte.reporttemplate.domain.ColumnChartTemplate;
import org.reporte.reporttemplate.domain.LineChartTemplate;
import org.reporte.reporttemplate.domain.PieChartTemplate;
import org.reporte.reporttemplate.domain.ReportQuery;
import org.reporte.reporttemplate.domain.TemplateSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//stateless session bean
@Stateless
//container managed transaction manager
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ReportGenerationServiceImpl implements ReportGenerationService{
	
	private final Logger LOG = LoggerFactory.getLogger(ReportGenerationServiceImpl.class);
	
	@Inject
	private ReportTemplateDAO reportTemplateDAO;
	
	@Inject
	private ReportQueryDAO reportQueryDAO;
	
	@Inject
	private JdbcClient jdbcClient;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AreaChartReport generateAreaChartReport(int reportTemplateId) throws ReportGenerationServiceException{
		return (AreaChartReport)generateCartesiantChartReport(reportTemplateId, new AreaChartReport());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AreaChartReport generateAreaChartReport(AreaChartTemplate reportTemplate) throws ReportGenerationServiceException{
		return (AreaChartReport)generateCartesiantChartReport(reportTemplate, new AreaChartReport());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BarChartReport generateBarChartReport(int reportTemplateId) throws ReportGenerationServiceException{
		return (BarChartReport)generateCartesiantChartReport(reportTemplateId, new BarChartReport());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BarChartReport generateBarChartReport(BarChartTemplate reportTemplate) throws ReportGenerationServiceException{
		return (BarChartReport)generateCartesiantChartReport(reportTemplate, new BarChartReport());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ColumnChartReport generateColumnChartReport(int reportTemplateId) throws ReportGenerationServiceException{
		return (ColumnChartReport)generateCartesiantChartReport(reportTemplateId, new ColumnChartReport());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ColumnChartReport generateColumnChartReport(ColumnChartTemplate reportTemplate) throws ReportGenerationServiceException{
		return (ColumnChartReport)generateCartesiantChartReport(reportTemplate, new ColumnChartReport());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LineChartReport generateLineChartReport(int reportTemplateId) throws ReportGenerationServiceException{
		return (LineChartReport)generateCartesiantChartReport(reportTemplateId, new LineChartReport());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LineChartReport generateLineChartReport(LineChartTemplate reportTemplate) throws ReportGenerationServiceException{
		return (LineChartReport)generateCartesiantChartReport(reportTemplate, new LineChartReport());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PieChartReport generatePieChartReport(int reportTemplateId) throws ReportGenerationServiceException{
		PieChartReport report = null;
		try{
			//1. obtain the template
			PieChartTemplate pieChartTemplate = (PieChartTemplate)reportTemplateDAO.find(reportTemplateId);
			
			//2. generate pie chart report based on template
			report = generatePieChartReport(pieChartTemplate);
		}
		catch(BaseDAOException bde){
			throw new ReportGenerationServiceException("Failed to generate pie chart Report for "+reportTemplateId, bde);
		}
		
		return report;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PieChartReport generatePieChartReport(PieChartTemplate reportTemplate) throws ReportGenerationServiceException{
		PieChartReport report = null;
		try{
			//1. populate report with template setting
			report = new PieChartReport();
			populatePieChartReport(report, reportTemplate);
			
			//2. generate pie chart report
			populatePieChartReportCategoryResult(report, reportTemplate);
		}
		catch (JdbcClientException jce) {
			throw new ReportGenerationServiceException("Failed to generate pie char report for "+ reportTemplate.getTemplateName(), jce);
		} catch (BaseDAOException bde) {
			throw new ReportGenerationServiceException("Failed to generate pie char report for "+ reportTemplate.getTemplateName(), bde);
		}
		
		return report;
	}
	
	/**
	 * 
	 * @param report
	 * @param template
	 */
	private void populatePieChartReport(PieChartReport report, PieChartTemplate template){
		//report name
		report.setReportName(template.getReportDisplayName());

		//chart title
		report.setTitle(template.getTitle());
		
		//show legend
		report.setShowLegend(template.isShowLegend());
		
		//show data label
		report.setShowDataLabel(template.isShowDataLabel());
		
		//data type for show data label case
		if(report.isShowDataLabel()){
			report.setDataTypeFormat(template.getDataTypeFormat());
		}
		
		//initialize Category data map for later usage
		report.setCategoryData(new HashMap<String, Number>());
		
		report.setReportType(template.getReportTemplateType());
	}
	
	/**
	 * 
	 * @param report
	 * @param chartTemplate
	 * @throws ReportQueryDAOException
	 * @throws JdbcClientException
	 */
	private void  populatePieChartReportCategoryResult(PieChartReport report, PieChartTemplate chartTemplate) 
			throws ReportQueryDAOException, JdbcClientException{
		
		Map<String, Number> categoryDataMap = report.getCategoryData();
		
		ReportQuery reportQuery = reportQueryDAO.find(chartTemplate.getId());
		
		List<Map<ColumnMetadata, String>> resultList = jdbcClient.execute(reportQuery.getDatasource(), reportQuery.getQuery());
		
		String modelCategoryField = chartTemplate.getModelCategoryField();
		String modelDataField = chartTemplate.getModelDataField();
		
		//for each result row
		for(Map<ColumnMetadata, String> row: resultList){
			String categoryName = null;
			Number categoryValue = null;
			
			//for each field in the row
			for(Map.Entry<ColumnMetadata, String> rowField : row.entrySet()){
				String fieldLabel = rowField.getKey().getLabel();
				
				if(fieldLabel!=null){
					if(fieldLabel.equals(modelCategoryField)){
						categoryName = rowField.getValue();
					}
					else if (fieldLabel.equals(modelDataField)){
						categoryValue = convertToNumber(rowField.getKey().getTypeName(), rowField.getValue());
					}
				}
			}
			
			
			if(StringUtils.isNoneBlank(categoryName) && 
			   categoryValue!=null){
				categoryDataMap.put(categoryName, categoryValue);
			}
		}
	}
	
	/**
	 * 
	 * @param reportTemplateId
	 * @param report
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	private CartesianChartReport generateCartesiantChartReport(int reportTemplateId, CartesianChartReport report) 
			throws ReportGenerationServiceException{
		
		try{
			//1. obtain the template
			CartesianChartTemplate chartTemplate = (CartesianChartTemplate)reportTemplateDAO.find(reportTemplateId);
			
			//2. generate cartesian chart report based on template
			generateCartesiantChartReport(chartTemplate, report);
		}
		catch(BaseDAOException bde){
			throw new ReportGenerationServiceException("Failed to generate cartesian chart report for "+reportTemplateId, bde);
		}

		return report;
	}
	
	/**
	 * 
	 * @param chartTemplate
	 * @param report
	 * @return
	 * @throws ReportGenerationServiceException
	 */
	private CartesianChartReport generateCartesiantChartReport(CartesianChartTemplate chartTemplate, CartesianChartReport report) 
			throws ReportGenerationServiceException{
		
		try{
			//1. populate report with template setting
			populateCartesianReport(report, chartTemplate);
			
			//2. populate report with query result
			populateCartesianReportSeriesResult(report, chartTemplate);
		}
		catch (JdbcClientException jce) {
			throw new ReportGenerationServiceException("Failed to generate cartesian chart report for "+chartTemplate.getTemplateName(), jce);
		}
		catch(BaseDAOException bde){
			throw new ReportGenerationServiceException("Failed to generate cartesian chart report for "+chartTemplate.getTemplateName(), bde);
		} 
		
		return report;
	}
	/**
	 * 
	 * @param report
	 * @param chartTemplate
	 */
	private void populateCartesianReport(CartesianChartReport report, CartesianChartTemplate chartTemplate){
		
		//Report name
		report.setReportName(chartTemplate.getReportDisplayName());
		
		//chart title
		report.setTitle(chartTemplate.getTitle());
		
		//X-Axis title
		report.setShowXAxis(chartTemplate.isShowXAxis());
		
		if(report.isShowXAxis()){
			report.setXAxisTitle(chartTemplate.getXAxisTitle());
		}
		
		//Y-Axis title
		report.setShowYAxis(chartTemplate.isShowYAxis());
		
		if(report.isShowYAxis()){
			report.setYAxisTitle(chartTemplate.getYAxisTitle());
		}
		
		//show data label
		report.setShowDataLabel(chartTemplate.isShowDataLabel());
		
		//show legend
		report.setShowLegend(chartTemplate.isShowLegend());
		
		//report type
		report.setReportType(chartTemplate.getReportTemplateType());
	}
	
	/**
	 * 
	 * @param report
	 * @param chartTemplate
	 * @throws JdbcClientException
	 * @throws ReportQueryDAOException
	 */
	private void populateCartesianReportSeriesResult(CartesianChartReport report, CartesianChartTemplate chartTemplate) 
			throws JdbcClientException, ReportQueryDAOException{
		
		report.setChartDataSeries(new ArrayList<ChartSeries>());
		
		ReportQuery reportQuery = reportQueryDAO.find(chartTemplate.getId());
		
		List<Map<ColumnMetadata, String>> resultList = jdbcClient.execute(reportQuery.getDatasource(), reportQuery.getQuery());
		
		String modelDataLabelField = chartTemplate.getModelDataLabelField();
		String modelDataValueField = chartTemplate.getModelDataValueField();
		String modelSeriesGroupField = chartTemplate.getModelSeriesGroupField();
		
		Map<String, ChartSeries> seriesLookupMap = prepareSeriesLookupMap(report, chartTemplate);
		
		//for each result row
		for(Map<ColumnMetadata, String> row: resultList){
			String seriesName = null;
			String dataLabel = null;
			Number dataValue = null;
			
			//for each field in the row
			for(Map.Entry<ColumnMetadata, String> rowField : row.entrySet()){
				String fieldLabel = rowField.getKey().getLabel();
				
				if(fieldLabel!=null){
					//if the field belongs to dataLabel
					if(fieldLabel.equals(modelDataLabelField)){
						dataLabel = rowField.getValue();
					}
					else if(fieldLabel.equals(modelDataValueField)){
						dataValue = convertToNumber(rowField.getKey().getTypeName(), rowField.getValue());
					}
					else if (fieldLabel.equals(modelSeriesGroupField)){
						seriesName = rowField.getValue();
					}
				}
			}
			
			//if all are valid
			if(StringUtils.isNoneBlank(seriesName) &&
			   StringUtils.isNoneBlank(dataLabel) &&
			   dataValue!=null){
				//lookup the series to store the value
				ChartSeries series = seriesLookupMap.get(seriesName);
				
				if(series!=null){
					series.getSeriesData().put(dataLabel, dataValue);
				}
			}
		}
	}
	
	/**
	 * convert the value according to type to java.lang.Number
	 * @param valueType
	 * @param valueStr
	 * @return
	 */
	private Number convertToNumber(String valueType, String valueStr){
		Number convertedNumber = null;
		
		SqlTypeEnum sqlType = SqlTypeEnum.fromString(valueType);
		
		if(sqlType!=null && valueStr !=null){
			try{
				switch(sqlType){
					case BIGINT:
						convertedNumber = new BigInteger(valueStr);
						break;
					case DECIMAL:
					case NUMERIC:
						convertedNumber = new BigDecimal(valueStr);
						break;
					case DOUBLE:
					case FLOAT:
						convertedNumber = Double.valueOf(valueStr);
						break;
					case INTEGER:
						convertedNumber = Integer.valueOf(valueStr);
						break;
					case REAL:
						convertedNumber = Float.valueOf(valueStr);
						break;
					case SMALLINT:
						convertedNumber = Short.valueOf(valueStr);
						break;
					case TINYINT:
						convertedNumber = Byte.valueOf(valueStr);
						break;
					default:
						break;
				}
			}
			catch(NumberFormatException nfe){
				LOG.info(valueStr+ " can't be converted to Number type "+valueType, nfe);
			}
		}
		
		return convertedNumber;
	}
	/**
	 * 
	 * @param report
	 * @param chartTemplate
	 * @return
	 */
	private Map<String, ChartSeries> prepareSeriesLookupMap(CartesianChartReport report, CartesianChartTemplate chartTemplate){
		Map<String, ChartSeries> seriesLookupMap = new HashMap<String, ChartSeries>();
		
		//for each of the template defined series
		for(TemplateSeries series: chartTemplate.getDataSeries()){
			//if not yet registered
			if(seriesLookupMap.get(series.getName())==null){
				//create a new entry and register
				ChartSeries chartSeries = new ChartSeries();
				//initialize with empty series data
				chartSeries.setSeriesData(new HashMap<String, Number>());
				chartSeries.setSeriesName(series.getName());
				seriesLookupMap.put(series.getModelSeriesValue(), chartSeries);
				
				report.getChartDataSeries().add(chartSeries);
			}
		}
		
		return seriesLookupMap;
	}
	
}