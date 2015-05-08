package org.reportbay.api.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.reportbay.api.dto.report.RestReport;
import org.reportbay.api.dto.report.RestReports;
import org.reportbay.api.dto.reportconnector.RestLiteReportConnector;
import org.reportbay.api.dto.reportconnector.RestReportConnector;
import org.reportbay.api.dto.reportconnector.RestReportConnectors;
import org.reportbay.api.dto.reportconnector.TemplateType;
import org.reportbay.api.service.ReportConnectorService;
import org.reportbay.api.service.exception.ReportConnectorServiceException;
import org.reportbay.report.domain.CrossTabReport;
import org.reportbay.report.service.ReportGenerationService;
import org.reportbay.report.service.exception.ReportGenerationServiceException;
import org.reportbay.reporttemplate.domain.AreaChartTemplate;
import org.reportbay.reporttemplate.domain.BarChartTemplate;
import org.reportbay.reporttemplate.domain.BaseReportTemplate;
import org.reportbay.reporttemplate.domain.CartesianChartTemplate;
import org.reportbay.reporttemplate.domain.ChartTemplate;
import org.reportbay.reporttemplate.domain.ColumnChartTemplate;
import org.reportbay.reporttemplate.domain.CrossTabTemplate;
import org.reportbay.reporttemplate.domain.LineChartTemplate;
import org.reportbay.reporttemplate.domain.PieChartTemplate;
import org.reportbay.reporttemplate.domain.ReportQuery;
import org.reportbay.reporttemplate.domain.ReportTemplateTypeEnum;
import org.reportbay.reporttemplate.domain.TemplateDiscriminatorConstants;
import org.reportbay.reporttemplate.domain.TemplateSeries;
import org.reportbay.reporttemplate.service.ReportTemplateService;
import org.reportbay.reporttemplate.service.exception.ReportTemplateServiceException;
import org.reportbay.snapshot.domain.ReportSnapShot;
import org.reportbay.snapshot.domain.ReportSnapShotBase;
import org.reportbay.snapshot.service.SnapShotService;
import org.reportbay.snapshot.service.exception.SnapShotServiceException;
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
	private SnapShotService reportSnapShotService;
	
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
			LOG.warn("unable to find record for id {}",reportConnectorId);
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
    		connector.setReportDisplayName(reportTemplate.getReportDisplayName());

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
    		connector.setType(TemplateType.AREA);
    		connector.setCartesianChartTemplate((CartesianChartTemplate)template);
    	}
    	else if (template instanceof BarChartTemplate){
    		connector.setType(TemplateType.BAR);
    		connector.setCartesianChartTemplate((CartesianChartTemplate)template);
    	}
    	else if (template instanceof ColumnChartTemplate){
    		connector.setType(TemplateType.COLUMN);
    		connector.setCartesianChartTemplate((CartesianChartTemplate)template);
    	}
    	else if (template instanceof LineChartTemplate){
    		connector.setType(TemplateType.LINE);
    		connector.setCartesianChartTemplate((CartesianChartTemplate)template);
    	}
    	else if (template instanceof PieChartTemplate){
    		connector.setType(TemplateType.PIE);
    		connector.setPieChartTemplate((PieChartTemplate)template);
    	}else if(template instanceof CrossTabTemplate){
    		connector.setType(TemplateType.CROSSTAB);
    		connector.setCrossTabTemplate((CrossTabTemplate) template);
    	}
    	
    	return connector;
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
    	if(connector.getType()!=null){
    		TemplateType type = connector.getType();
    		
    		switch(type){
    			case AREA:
    			case BAR:
    			case COLUMN:
    			case LINE:
    			case PIE:
    				reportTemplate = createChartTemplateFromRestReportConnector(connector);
    				break;
    			case CROSSTAB:
    				LOG.info("Creating CrossTabTemplate from ReportConnector");
    				reportTemplate = createCrossTabFromReportConnector(connector);
    				break;
    				
   	        	/*** reserved for other type of template **/
    				
    			default:
    				//TODO: unrecognized type error
    				break;
    		}
   		
    	}
    	return reportTemplate;
    }
    
    private CrossTabTemplate createCrossTabFromReportConnector(RestReportConnector connector){
    	CrossTabTemplate template = connector.getCrossTabTemplate();
    	return template;
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
    	
    	TemplateType chartType = connector.getType();
    	
    	if(chartType!=null){
    		ReportQuery refinedReportQuery = null;
    		
	    	switch(chartType){
	    		case AREA:
	    			template = new AreaChartTemplate();
	    			mapReportConnectorToCartesianChartTemplate(connector.getCartesianChartTemplate(), (CartesianChartTemplate)template);
	    			refinedReportQuery = reportTemplateService.constructReportQuery((AreaChartTemplate)template);
	    			break;
	    		case BAR:
	    			template = new BarChartTemplate();
	    			mapReportConnectorToCartesianChartTemplate(connector.getCartesianChartTemplate(), (CartesianChartTemplate)template);
	    			refinedReportQuery = reportTemplateService.constructReportQuery((BarChartTemplate)template);
	    			break;
	    		case COLUMN:
	    			template = new ColumnChartTemplate();
	    			mapReportConnectorToCartesianChartTemplate(connector.getCartesianChartTemplate(), (CartesianChartTemplate)template);
	    			refinedReportQuery = reportTemplateService.constructReportQuery((ColumnChartTemplate)template);
	    			break;
	    		case LINE:
	    			template = new LineChartTemplate();
	    			mapReportConnectorToCartesianChartTemplate(connector.getCartesianChartTemplate(), (CartesianChartTemplate)template);
	    			refinedReportQuery = reportTemplateService.constructReportQuery((LineChartTemplate)template);
	    			break;
	    		case PIE:
	    			template = connector.getPieChartTemplate();
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
    private void mapReportConnectorToCartesianChartTemplate(CartesianChartTemplate source, CartesianChartTemplate target){
    	//1. map chart attribute
    	mapReportConnectorToChartTemplate(source, target);
    	
    	//2. map cartesian chart attributes
    	target.setReportTemplateType(ReportTemplateTypeEnum.SIMPLE);
    	target.setShowDataLabel(source.isShowDataLabel());

    	target.setShowXAxis(source.isShowXAxis());
    	target.setShowYAxis(source.isShowYAxis());

    	target.setXAxisTitle(source.getXAxisTitle());
    	target.setYAxisTitle(source.getYAxisTitle());
    	
    	target.setModelDataLabelField(source.getModelDataLabelField());
    	target.setModelDataValueField(source.getModelDataValueField());

    	target.setModelSeriesGroupField(source.getModelSeriesGroupField());
    	
    	target.setDataSeries(new LinkedHashSet<TemplateSeries>());
    	
    	if(CollectionUtils.isNotEmpty(source.getDataSeries())){
    		target.getDataSeries().addAll(source.getDataSeries());
    	}
    }

    /**
     * 
     * @param connector
     * @param template
     */
    private void mapReportConnectorToChartTemplate(ChartTemplate source, ChartTemplate target){
    	//1. base attribute mapping
    	mapReportConnectorToBaseReportTemplate(source, target);
    	
    	//2. map chart attributes
    	target.setTitle(source.getTitle());
    	target.setShowLegend(source.isShowLegend());
    }
    
    /**
     * 
     * @param connector
     * @param template
     */
    private void mapReportConnectorToBaseReportTemplate(BaseReportTemplate source, BaseReportTemplate target){
    	
    	target.setId(source.getId());
    	target.setTemplateName(source.getTemplateName());
    	target.setReportDisplayName(source.getReportDisplayName());
    	target.setModelId(source.getModelId());
    }
    /**
     * 
     * @param reportTemplate
     * @return
     * @throws ReportConnectorServiceException
     */
    private RestReport generateReport(BaseReportTemplate reportTemplate) throws ReportConnectorServiceException{
    	RestReport restReport = new RestReport();
    	
    	try {
			if(reportTemplate instanceof AreaChartTemplate){
				restReport.setType(TemplateType.AREA.name());
				restReport.setCartesianChartReport(reportGenerationService.generateAreaChartReport((AreaChartTemplate)reportTemplate));
			}
			else if (reportTemplate instanceof BarChartTemplate){
				restReport.setType(TemplateType.BAR.name());
				restReport.setCartesianChartReport(reportGenerationService.generateBarChartReport((BarChartTemplate)reportTemplate));
			}
			else if (reportTemplate instanceof ColumnChartTemplate){
				restReport.setType(TemplateType.COLUMN.name());
				restReport.setCartesianChartReport(reportGenerationService.generateColumnChartReport((ColumnChartTemplate)reportTemplate));
			}
			else if (reportTemplate instanceof LineChartTemplate){
				restReport.setType(TemplateType.LINE.name());
				restReport.setCartesianChartReport(reportGenerationService.generateLineChartReport((LineChartTemplate)reportTemplate));
			}
			else if (reportTemplate instanceof PieChartTemplate){
				restReport.setType(TemplateType.PIE.name());
				restReport.setPieChartReport(reportGenerationService.generatePieChartReport((PieChartTemplate)reportTemplate));
			}
			else if(reportTemplate instanceof CrossTabTemplate){
				LOG.info("Generate Report for CrossTabTemplate...");
				CrossTabTemplate crossTabTemplate = (CrossTabTemplate) reportTemplate;
				restReport.setType(TemplateType.CROSSTAB.name());
				Optional<ReportQuery> reportQuery = reportTemplateService.constructReportQuery(crossTabTemplate);
				if (reportQuery.isPresent()) {
					LOG.info("Generated Query {}",reportQuery.get().getQuery());
					//Set the Report Query to crosstabtemplate
					crossTabTemplate.setReportQuery(reportQuery.get());
					Optional<CrossTabReport> crossTabReport = reportGenerationService.generateCrossTabReport(crossTabTemplate);
					if (crossTabReport.isPresent()) {
						LOG.info("CrossTabReport is Not Null");
						restReport.setCrossTabReport(crossTabReport.get());
					}else{
						LOG.error("CrossTabReport Object is Empty");
						restReport.setCrossTabReport(null);
					}
				}else{
					LOG.error("CrossTabReport object is Null for ----------> generateReport");
					restReport.setCrossTabReport(null);
				}
			}
			else{
				throw new ReportConnectorServiceException("Unregconized type template");
			}
		} catch (ReportGenerationServiceException | ReportTemplateServiceException e) {
			throw new ReportConnectorServiceException("Error Generating Report...", e);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestReport generateReportSnapshot(int reportConnectorId) throws ReportConnectorServiceException {
		
		RestReport report = null;

		try{
			//1. retrieve the template base on the id
    		BaseReportTemplate reportTemplate = reportTemplateService.findReportTemplate(reportConnectorId);
    		
    		if(reportTemplate == null){
    			throw new ReportConnectorServiceException("report template not found for "+reportConnectorId);
    		}
    		//2. generate the report based on template
    		report = generateReport(reportTemplate);
    		
    		//3. take a snap shot and store
    		ReportSnapShot reportSnapShot = new ReportSnapShot();
    		
    		reportSnapShot.setCreationDate(new Date());
    		reportSnapShot.setReportName(reportTemplate.getReportDisplayName());
    		reportSnapShot.setTemplateType(deriveTemplateType(reportTemplate));
    		reportSnapShot.setTemplateId(reportConnectorId);
    		reportSnapShot.setSnapShot(SerializationUtils.serialize(report));
    		
    		reportSnapShotService.save(reportSnapShot);

    		
		}
		catch(SnapShotServiceException ssse){
			throw new ReportConnectorServiceException("exception in saving report snapshot for  "+reportConnectorId,ssse);
		}
		catch(ReportTemplateServiceException rtse ){
			throw new ReportConnectorServiceException("exception in finding report template for  "+reportConnectorId,rtse);
		}
		
		return report;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestReport getReportSnapshot(int reportId) throws ReportConnectorServiceException {
		
		RestReport report = null;
		
		try {
			ReportSnapShot reportSnapShot = reportSnapShotService.findReportSnapShot(reportId);
			
			report = (RestReport)SerializationUtils.deserialize(reportSnapShot.getSnapShot());
		} 
		catch (SnapShotServiceException e) {
			throw new ReportConnectorServiceException("exception in finding snap shot for report",e);
		}
		
		return report;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestReports getReports() throws ReportConnectorServiceException {
		
		RestReports reports = new RestReports();
		
		try {
			//TODO: by profile
			
			//1. obtain all snap shot reports
			List<ReportSnapShotBase> reportSnapShotBaseList = reportSnapShotService.findAllReportSnapShotBase();
			
			reports.getLiteReports().addAll(reportSnapShotBaseList);
			
			//2. obtain all on demand report (report template)
			List<BaseReportTemplate> reportTemplateList = reportTemplateService.findAllReportTemplate();
			
			reports.getLiteReports().addAll(mapReportTemplatesToLiteReports(reportTemplateList));
		} 
		catch (SnapShotServiceException ssse) {
			throw new ReportConnectorServiceException("exception in get report snapshots ", ssse);
		} 
		catch (ReportTemplateServiceException rtse) {
			throw new ReportConnectorServiceException("exception in get report connector ", rtse);
		}

		return reports;
	}
	
	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportConnectorServiceException
	 */
	private char deriveTemplateType(BaseReportTemplate reportTemplate) throws ReportConnectorServiceException{
		if(reportTemplate instanceof AreaChartTemplate){
			return TemplateDiscriminatorConstants.AREA.charAt(0);
		}
		else if (reportTemplate instanceof BarChartTemplate){
			return TemplateDiscriminatorConstants.BAR.charAt(0);
		}
		else if (reportTemplate instanceof ColumnChartTemplate){
			return TemplateDiscriminatorConstants.COLUMN.charAt(0);
		}
		else if (reportTemplate instanceof LineChartTemplate){
			return TemplateDiscriminatorConstants.LINE.charAt(0);
		}
		else if (reportTemplate instanceof PieChartTemplate){
			return TemplateDiscriminatorConstants.PIE.charAt(0);
		}
		else if(reportTemplate instanceof CrossTabTemplate){
			return TemplateDiscriminatorConstants.CROSSTAB.charAt(0);
		}
		else{
			throw new ReportConnectorServiceException("Unrecognized template type");
		}
	}
	
	/**
	 * convert the reporttemplate in report snap shot form (exclude id and creation date)
	 * @param reportTemplateList
	 * @return
	 * @throws ReportConnectorServiceException
	 */
	private List<ReportSnapShotBase> mapReportTemplatesToLiteReports(List<BaseReportTemplate> reportTemplateList) 
		throws ReportConnectorServiceException{

		List<ReportSnapShotBase> reportSnapShotBaseList = new ArrayList<ReportSnapShotBase>();
		
		for(BaseReportTemplate reportTemplate: reportTemplateList){
			if(reportTemplate!=null){
				ReportSnapShotBase report = new ReportSnapShotBase();
				
				report.setReportName(reportTemplate.getReportDisplayName());
				report.setTemplateId(reportTemplate.getId());
				report.setTemplateType(deriveTemplateType(reportTemplate));
				
				reportSnapShotBaseList.add(report);
			}
		}
		
		return reportSnapShotBaseList;
	}
}