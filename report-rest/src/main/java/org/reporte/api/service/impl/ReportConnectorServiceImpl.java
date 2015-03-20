package org.reporte.api.service.impl;

import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.reporte.api.dto.model.RestModel;
import org.reporte.api.dto.report.RestReport;
import org.reporte.api.dto.reportconnector.RestChartTypeEnum;
import org.reporte.api.dto.reportconnector.RestLiteReportConnector;
import org.reporte.api.dto.reportconnector.RestReportConnector;
import org.reporte.api.dto.reportconnector.RestReportConnectors;
import org.reporte.api.rest.exception.CustomizedWebException;
import org.reporte.api.service.ReportConnectorService;
import org.reporte.api.service.exception.ReportConnectorServiceException;
import org.reporte.model.domain.Model;
import org.reporte.model.domain.SimpleModel;
import org.reporte.model.service.ModelService;
import org.reporte.model.service.exception.ModelServiceException;
import org.reporte.report.service.ReportGenerationService;
import org.reporte.report.service.exception.ReportGenerationServiceException;
import org.reporte.reporttemplate.domain.AreaChartTemplate;
import org.reporte.reporttemplate.domain.BarChartTemplate;
import org.reporte.reporttemplate.domain.BaseReportTemplate;
import org.reporte.reporttemplate.domain.CartesianChartTemplate;
import org.reporte.reporttemplate.domain.ChartTemplate;
import org.reporte.reporttemplate.domain.ColumnChartTemplate;
import org.reporte.reporttemplate.domain.LineChartTemplate;
import org.reporte.reporttemplate.domain.PieChartDataTypeEnum;
import org.reporte.reporttemplate.domain.PieChartTemplate;
import org.reporte.reporttemplate.domain.ReportQuery;
import org.reporte.reporttemplate.domain.ReportTemplateTypeEnum;
import org.reporte.reporttemplate.domain.TemplateSeries;
import org.reporte.reporttemplate.service.ReportTemplateService;
import org.reporte.reporttemplate.service.exception.ReportTemplateServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * helper service on report connector implementation
 *
 */
@Named
public class ReportConnectorServiceImpl implements ReportConnectorService{
	
	private final Logger LOG = LoggerFactory.getLogger(ReportConnectorServiceImpl.class);
	
	@Inject
	private ReportTemplateService reportTemplateService;
	
	@Inject
	private ReportGenerationService reportGenerationService;
	
	@Inject
	private ModelService modelService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestReportConnectors findAllReportConnectors() throws ReportConnectorServiceException {
		
		RestReportConnectors connectors = new RestReportConnectors();
		
		try {
    		List<BaseReportTemplate> allReportTemplates = reportTemplateService.findAllReportTemplate();
			
			if(allReportTemplates!=null){
				for(BaseReportTemplate reportTemplate: allReportTemplates){
					mapBaseReportTemplateToRestLiteReportConnectors(connectors, reportTemplate);
				}
			}
		} 
    	catch (ReportTemplateServiceException e) {
			throw new ReportConnectorServiceException("Exception in findAllReportConnectors" ,e);
		}
		
		return connectors;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestReportConnector find(int reportConnectorId) throws ReportConnectorServiceException {
		
		BaseReportTemplate reportTemplate = null;
		try {
    		//1. retrieve domain report template by id
			reportTemplate = reportTemplateService.findReportTemplate(reportConnectorId);
		} 
		catch (ReportTemplateServiceException e) {
			throw new ReportConnectorServiceException("Exception in find " ,e);
		}
		
		if(reportTemplate==null){
			LOG.warn("unable to find record for id "+reportConnectorId);
			throw new ReportConnectorServiceException("record not found");
		}
		//2. converted to REST domain report connector
		return createRestReportConnectorFromBaseReportTemplate(reportTemplate);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestReport generateReportPreview(int reportConnectorId) throws ReportConnectorServiceException {
		RestReport report = null;
		
		try{
			//1. retrieve the template base on the id
    		BaseReportTemplate reportTemplate = reportTemplateService.findReportTemplate(reportConnectorId);
    		
    		if(reportTemplate == null){
    			throw new ReportConnectorServiceException("report template not found for "+reportConnectorId);
    		}
    		
    		//2. generate report based on template
    		report = generateReport(reportTemplate);
		}
		catch(ReportTemplateServiceException rtse){
			throw new ReportConnectorServiceException("exception in finding report template for  "+reportConnectorId,rtse);
		}
		catch(ReportGenerationServiceException rgse){
			throw new ReportConnectorServiceException("exception in generating report preview ",rgse);
		}
		
		return report;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestReport generateReportPreview(RestReportConnector restReportConnector) throws ReportConnectorServiceException {
		RestReport report = null;
		
		try {
			BaseReportTemplate reportTemplate = createBaseReportTemplateFromRestReportConnector(restReportConnector);
			
			//2. generate report based on template
    		report = generateReport(reportTemplate);
		} 
		catch(ReportTemplateServiceException rtse){
			throw new ReportConnectorServiceException("exception in create report template from rest ",rtse);
		}
		catch(ReportGenerationServiceException rgse){
			throw new ReportConnectorServiceException("exception in generating report preview ",rgse);
		}
		return report;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestReportConnector save(RestReportConnector restReportConnector) throws ReportConnectorServiceException {
		RestReportConnector resultConnector = null;
		
		try{
			//1. convert REST to entity
			BaseReportTemplate reportTemplate = createBaseReportTemplateFromRestReportConnector(restReportConnector);
			
			if(reportTemplate == null){
				throw new ReportConnectorServiceException("Failed to convert to known type template ");
			}
			
			//2. save entity
			BaseReportTemplate resultTemplate = save(reportTemplate);
			
			//3. convert result entity to REST
			resultConnector = createRestReportConnectorFromBaseReportTemplate(resultTemplate);
		}
		catch(ReportTemplateServiceException rte){
			throw new ReportConnectorServiceException("Exception in saving report connector ",rte);
		}
		return resultConnector;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(int reportConnectorId) throws ReportConnectorServiceException {
		BaseReportTemplate reportTemplate = null;
		
		//1. check entity exist for deletion
		try {
			reportTemplate = reportTemplateService.findReportTemplate(reportConnectorId);
		} 
		catch (ReportTemplateServiceException e) {
			throw new ReportConnectorServiceException("Error finding report connector "+reportConnectorId,e);
		}
		
		if(reportTemplate==null){
			throw new ReportConnectorServiceException("Error finding report connector "+reportConnectorId);
		}
		
		//2. delete the entity
		try {
			reportTemplateService.deleteReportTemplate(reportTemplate);
		}
		catch(ReportTemplateServiceException e) {
			throw new ReportConnectorServiceException("Error deleting report connector "+reportConnectorId,e);
		}
	}
	
	@Override
	public RestReportConnector update(RestReportConnector restReportConnector) throws ReportConnectorServiceException {
		RestReportConnector resultConnector = null;
		
		try{
			//1. convert REST to entity
			BaseReportTemplate reportTemplate = createBaseReportTemplateFromRestReportConnector(restReportConnector);
			
			if(reportTemplate == null){
				throw new ReportConnectorServiceException("Failed to convert to known type template ");
			}
			
			//2. save entity
			BaseReportTemplate resultTemplate = update(reportTemplate);
			
			//3. convert result entity to REST
			resultConnector = createRestReportConnectorFromBaseReportTemplate(resultTemplate);
		}
		catch(ReportTemplateServiceException rte){
			throw new ReportConnectorServiceException("Exception in saving report connector ",rte);
		}
		return resultConnector;	
	}
	/*************** private methods ****************/
    
    /**
     * 
     * @param connectors
     * @param reportTemplate
     */
    private void mapBaseReportTemplateToRestLiteReportConnectors(RestReportConnectors connectors, BaseReportTemplate reportTemplate){
    	
    	if(reportTemplate !=null){
    		RestLiteReportConnector connector = new RestLiteReportConnector();
    		
    		connector.setId(reportTemplate.getId());
    		connector.setName(reportTemplate.getTemplateName());
    		
    		connectors.getConnectors().add(connector);
    	}
    }

    /**
     * 
     * @param template
     * @return
     */
    private RestReportConnector createRestReportConnectorFromBaseReportTemplate(BaseReportTemplate template){
    	RestReportConnector connector = new RestReportConnector();
    	
    	if(template instanceof AreaChartTemplate){
    		connector.setChartType(RestChartTypeEnum.AREA.name());
    		mapCartesianChartTemplateToReportConnector((CartesianChartTemplate)template, connector);
    	}
    	else if (template instanceof BarChartTemplate){
    		connector.setChartType(RestChartTypeEnum.BAR.name());
    		mapCartesianChartTemplateToReportConnector((CartesianChartTemplate)template, connector);
    	}
    	else if (template instanceof ColumnChartTemplate){
    		connector.setChartType(RestChartTypeEnum.COLUMN.name());
    		mapCartesianChartTemplateToReportConnector((CartesianChartTemplate)template, connector);
    	}
    	else if (template instanceof LineChartTemplate){
    		connector.setChartType(RestChartTypeEnum.LINE.name());
    		mapCartesianChartTemplateToReportConnector((CartesianChartTemplate)template, connector);
    	}
    	else if (template instanceof PieChartTemplate){
    		connector.setChartType(RestChartTypeEnum.PIE.name());
    		mapPieChartTemplateToReportConnector((PieChartTemplate)template, connector);
    	}
    	
    	return connector;
    }

    /**
     * 
     * @param template
     * @param connector
     */
    private void mapPieChartTemplateToReportConnector(PieChartTemplate template, RestReportConnector connector){
    	//1. map chart attributes
    	mapChartTemplateToReportConnector(template, connector);
    	
    	//2. map pie chart attributes
    	connector.setCategoryName(template.getCategoryName());
    	connector.setModelCategoryField(template.getModelCategoryField());
    	connector.setModelDataField(template.getModelDataField());
    	connector.setDataTypeFormat(template.getDataTypeFormat()==null?null:template.getDataTypeFormat().name());
    	connector.setShowDataLabel(template.isShowDataLabel());
    }
    
    
    /**
     * 
     * @param template
     * @param connector
     */
    private void mapCartesianChartTemplateToReportConnector(CartesianChartTemplate template, RestReportConnector connector){
    	//1. map chart attributes
    	mapChartTemplateToReportConnector(template, connector);

    	//2. map cartesian chart attributes
    	connector.setxAxisTitle(template.getXAxisTitle());
    	connector.setyAxisTitle(template.getYAxisTitle());
    	connector.setShowXAxis(template.isShowXAxis());
    	connector.setShowYAxis(template.isShowXAxis());
    	connector.setShowDataLabel(template.isShowDataLabel());
    	connector.setModelDataLabelField(template.getModelDataLabelField());
    	connector.setModelDataValueField(template.getModelDataValueField());
    	connector.setModelSeriesGroupField(template.getModelSeriesGroupField());
    	
    	connector.getTemplateSeries().addAll(template.getDataSeries());
    }
	
    /**
     * 
     * @param template
     * @param connector
     */
    private void mapChartTemplateToReportConnector(ChartTemplate template, RestReportConnector connector){
    	//1. map common base attributes
    	mapBaseReportTemplateToReportConnector(template, connector);
    	
    	//2. map chart attributes
    	connector.setChartTitle(template.getTitle());
    	connector.setShowLegend(template.getShowLegend());
    }
    /**
     * 
     * @param template
     * @param connector
     */
    private void mapBaseReportTemplateToReportConnector(BaseReportTemplate template, RestReportConnector connector){
    	
    	connector.setId(template.getId());
    	connector.setName(template.getTemplateName());
    	connector.setReportDisplayName(template.getReportDisplayName());
    	
    	try{
    		Model model = modelService.find(template.getModelId());
    		
    		if(model==null){
    			throw new ModelServiceException("model not found for id = "+template.getModelId());
    		}
    		
    		connector.setModel(createRestModelFromModel(model));
    	}
    	catch(ModelServiceException mse){
    		String errMsg = "Exception in finding report model "+template.getModelId();
    		LOG.error(errMsg, mse);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, errMsg);
    	}
    }
    
    /**
     * 
     * @param model
     * @return
     */
    private RestModel createRestModelFromModel(Model model){
    	RestModel restModel = new RestModel(model);
    	
    	if(model instanceof SimpleModel){
			restModel.setTable(((SimpleModel) model).getTable());
		}
    	return restModel;
    }
    
    
    /**
     * 
     * @param connector
     * @return
     * @throws ReportTemplateServiceException 
     */
    private BaseReportTemplate createBaseReportTemplateFromRestReportConnector(RestReportConnector connector) 
    		throws ReportTemplateServiceException{
    	
    	BaseReportTemplate reportTemplate = null;
    	
    	//1. create chart type template if chart type 
    	if(connector.getChartType()!=null){
    		reportTemplate = createChartTemplateFromRestReportConnector(connector);
    	}
    	//reserved for other type of template
    	
    	return reportTemplate;
    }
    
    /**
     * 
     * @param chartType
     * @param connector
     * @return
     * @throws ReportTemplateServiceException 
     */
    private ChartTemplate createChartTemplateFromRestReportConnector(RestReportConnector connector) 
    		throws ReportTemplateServiceException{
    	ChartTemplate template = null;
    	
    	RestChartTypeEnum chartType = RestChartTypeEnum.fromName(connector.getChartType());
    	
    	if(chartType!=null){
    		ReportQuery refinedReportQuery = null;
    		
	    	switch(chartType){
	    		case AREA:
	    			template = new AreaChartTemplate();
	    			mapReportConnectorToCartesianChartTemplate(connector, (CartesianChartTemplate)template);
	    			refinedReportQuery = reportTemplateService.constructReportQuery((AreaChartTemplate)template);
	    			break;
	    		case BAR:
	    			template = new BarChartTemplate();
	    			mapReportConnectorToCartesianChartTemplate(connector, (CartesianChartTemplate)template);
	    			refinedReportQuery = reportTemplateService.constructReportQuery((BarChartTemplate)template);
	    			break;
	    		case COLUMN:
	    			template = new ColumnChartTemplate();
	    			mapReportConnectorToCartesianChartTemplate(connector, (CartesianChartTemplate)template);
	    			refinedReportQuery = reportTemplateService.constructReportQuery((ColumnChartTemplate)template);
	    			break;
	    		case LINE:
	    			template = new LineChartTemplate();
	    			mapReportConnectorToCartesianChartTemplate(connector, (CartesianChartTemplate)template);
	    			refinedReportQuery = reportTemplateService.constructReportQuery((LineChartTemplate)template);
	    			break;
	    		case PIE:
	    			template = new PieChartTemplate();
	    			mapReportConnectorToPieChartTemplate(connector, (PieChartTemplate)template);
	    			refinedReportQuery = reportTemplateService.constructReportQuery((PieChartTemplate)template);
	    			break;
	    		default:
	    			//TODO: unrecognized type
	    			break;
	    	}
	    	
	    	if(template!=null){
	    		template.setReportQuery(refinedReportQuery);
	    	}
    	}
    	
    	return template;
    }
    
    
    /**
     * 
     * @param connector
     * @param template
     */
    private void mapReportConnectorToCartesianChartTemplate(RestReportConnector connector, CartesianChartTemplate template){
    	//1. map chart attribute
    	mapReportConnectorToChartTemplate(connector, template);
    	
    	//2. map cartesian chart attributes
    	template.setReportTemplateType(ReportTemplateTypeEnum.SIMPLE);
    	template.setShowDataLabel(connector.isShowDataLabel());

    	template.setShowXAxis(connector.isShowXAxis());
    	template.setShowXAxis(connector.isShowYAxis());

    	template.setXAxisTitle(connector.getxAxisTitle());
    	template.setYAxisTitle(connector.getyAxisTitle());
    	
    	template.setModelDataLabelField(connector.getModelDataLabelField());
    	template.setModelDataValueField(connector.getModelDataValueField());

    	template.setModelSeriesGroupField(connector.getModelSeriesGroupField());
    	
    	template.setDataSeries(new LinkedHashSet<TemplateSeries>());
    	template.getDataSeries().addAll(connector.getTemplateSeries());
    }
    /**
     * 
     * @param connector
     * @param template
     */
    private void mapReportConnectorToPieChartTemplate(RestReportConnector connector, PieChartTemplate template){
    	//1. map chart attribute
    	mapReportConnectorToChartTemplate(connector, template);
    	
    	//2. map pie chart attributes
    	template.setReportTemplateType(ReportTemplateTypeEnum.SIMPLE);
    	template.setShowDataLabel(connector.isShowDataLabel());
    	
    	template.setCategoryName(connector.getCategoryName());
    	template.setModelCategoryField(connector.getModelCategoryField());
    	template.setModelDataField(connector.getModelDataField());
    	template.setDataTypeFormat(PieChartDataTypeEnum.fromName(connector.getDataTypeFormat()));
    }
    /**
     * 
     * @param connector
     * @param template
     */
    private void mapReportConnectorToChartTemplate(RestReportConnector connector, ChartTemplate template){
    	//1. base attribute mapping
    	mapReportConnectorToBaseReportTemplate(connector, template);
    	
    	//2. map chart attributes
    	template.setTitle(connector.getChartTitle());
    	template.setShowLegend(connector.isShowLegend());
    }
    
    /**
     * 
     * @param connector
     * @param template
     */
    private void mapReportConnectorToBaseReportTemplate(RestReportConnector connector, BaseReportTemplate template){
    	template.setId(connector.getId());
    	template.setTemplateName(connector.getName());
    	template.setReportDisplayName(connector.getReportDisplayName());
    	
    	if(connector.getModel()!=null){
    		template.setModelId(connector.getModel().getId());
    	}
    }

    private RestReport generateReport(BaseReportTemplate reportTemplate) throws ReportGenerationServiceException{
    	RestReport restReport = new RestReport();
    	
    	if(reportTemplate instanceof AreaChartTemplate){
    		restReport.setType(RestChartTypeEnum.AREA.name());
			restReport.setCartesianChartReport(reportGenerationService.generateAreaChartReport((AreaChartTemplate)reportTemplate));
    	}
    	else if (reportTemplate instanceof BarChartTemplate){
    		restReport.setType(RestChartTypeEnum.BAR.name());
    		restReport.setCartesianChartReport(reportGenerationService.generateBarChartReport((BarChartTemplate)reportTemplate));
    	}
    	else if (reportTemplate instanceof ColumnChartTemplate){
    		restReport.setType(RestChartTypeEnum.COLUMN.name());
    		restReport.setCartesianChartReport(reportGenerationService.generateColumnChartReport((ColumnChartTemplate)reportTemplate));
    	}
    	else if (reportTemplate instanceof LineChartTemplate){
    		restReport.setType(RestChartTypeEnum.LINE.name());
    		restReport.setCartesianChartReport(reportGenerationService.generateLineChartReport((LineChartTemplate)reportTemplate));
    	}
    	else if (reportTemplate instanceof PieChartTemplate){
    		restReport.setType(RestChartTypeEnum.PIE.name());
    		restReport.setPieChartReport(reportGenerationService.generatePieChartReport((PieChartTemplate)reportTemplate));
    	}
    	else{
    		throw new ReportGenerationServiceException("Unregconized type template");
    	}
    	return restReport;
    }

    /**
     * 
     * @param template
     * @return
     * @throws ReportTemplateServiceException
     */
	private BaseReportTemplate save(BaseReportTemplate template) throws ReportTemplateServiceException{
		BaseReportTemplate resultTemplate = null;
		
		if(template instanceof AreaChartTemplate){
			resultTemplate = reportTemplateService.save((AreaChartTemplate)template);
		}
		else if (template instanceof BarChartTemplate){
			resultTemplate = reportTemplateService.save((BarChartTemplate)template);
		}
		else if (template instanceof ColumnChartTemplate){
			resultTemplate = reportTemplateService.save((ColumnChartTemplate)template);
		}
		else if (template instanceof LineChartTemplate){
			resultTemplate = reportTemplateService.save((LineChartTemplate)template);
		}
		else if (template instanceof PieChartTemplate){
			resultTemplate = reportTemplateService.save((PieChartTemplate)template);
		}
		//reserved for other type
		
		return resultTemplate;
	}
	
	/**
	 * 
	 * @param template
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private BaseReportTemplate update(BaseReportTemplate template) throws ReportTemplateServiceException{
		BaseReportTemplate resultTemplate = null;
		
		if(template instanceof AreaChartTemplate){
			resultTemplate = reportTemplateService.update((AreaChartTemplate)template);
		}
		else if (template instanceof BarChartTemplate){
			resultTemplate = reportTemplateService.update((BarChartTemplate)template);
		}
		else if (template instanceof ColumnChartTemplate){
			resultTemplate = reportTemplateService.update((ColumnChartTemplate)template);
		}
		else if (template instanceof LineChartTemplate){
			resultTemplate = reportTemplateService.update((LineChartTemplate)template);
		}
		else if (template instanceof PieChartTemplate){
			resultTemplate = reportTemplateService.update((PieChartTemplate)template);
		}
		//reserved for other type
		
		return resultTemplate;
	}
}