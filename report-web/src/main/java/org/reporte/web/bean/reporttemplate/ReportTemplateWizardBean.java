package org.reporte.web.bean.reporttemplate;

import static org.reporte.web.common.ChartTypeEnum.AREA;
import static org.reporte.web.common.ChartTypeEnum.BAR;
import static org.reporte.web.common.ChartTypeEnum.COLUMN;
import static org.reporte.web.common.ChartTypeEnum.LINE;
import static org.reporte.web.common.ChartTypeEnum.PIE;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.chart.ChartModel;
import org.reporte.common.domain.SqlTypeEnum;
import org.reporte.model.domain.AttributeMapping;
import org.reporte.model.domain.Model;
import org.reporte.model.service.ModelService;
import org.reporte.model.service.exception.ModelServiceException;
import org.reporte.report.domain.AreaChartReport;
import org.reporte.report.domain.BarChartReport;
import org.reporte.report.domain.ColumnChartReport;
import org.reporte.report.domain.LineChartReport;
import org.reporte.report.domain.PieChartReport;
import org.reporte.report.service.ReportGenerationService;
import org.reporte.report.service.exception.ReportGenerationServiceException;
import org.reporte.reporttemplate.domain.AreaChartTemplate;
import org.reporte.reporttemplate.domain.BarChartTemplate;
import org.reporte.reporttemplate.domain.BaseReportTemplate;
import org.reporte.reporttemplate.domain.CartesianChartTemplate;
import org.reporte.reporttemplate.domain.ChartTemplate;
import org.reporte.reporttemplate.domain.ColumnChartTemplate;
import org.reporte.reporttemplate.domain.LineChartTemplate;
import org.reporte.reporttemplate.domain.PieChartTemplate;
import org.reporte.reporttemplate.domain.ReportTemplateTypeEnum;
import org.reporte.reporttemplate.domain.TemplateSeries;
import org.reporte.reporttemplate.service.ReportTemplateService;
import org.reporte.reporttemplate.service.exception.ReportTemplateServiceException;
import org.reporte.web.common.ChartTypeEnum;
import org.reporte.web.util.ReportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Report Template Wizard JSF backing bean 
 *
 */
@Named("reportTemplateWizard")
@ViewScoped
public class ReportTemplateWizardBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int DUMMY_TEMPLATE_ID_FOR_QUERY_GEN = 100;
	
	private static final Logger LOG = LoggerFactory.getLogger(ReportTemplateWizardBean.class);

	private BaseReportTemplate reportTemplate;
	
	private String selectedChartType;
	
	private Map<String, String> chartTypeOptions;
	
	private String selectedModel;
	
	private Model model;
	private Map<String, String> availableModelOptions = new LinkedHashMap<String, String>();
	private Map<String, Model> availableModelMap = new HashMap<String, Model>();
	
	private boolean showModelDetails = false;
	private boolean isPieChartType = false;
	
	private String chartTypeImgSrc;
	
	private Map<String, String> dataValueFieldOptions = new LinkedHashMap<String, String>();
	private Map<String, String> modelFieldOptions = new LinkedHashMap<String, String>();
	
	private Map<String, String> templateSeriesValueOptions = new LinkedHashMap<String, String>();
	
	private boolean showLegend;
	private boolean showDataLabel;
	
	private ChartModel pfChartModel;
	private String pfChartType;
	
	@Inject
	private ModelService modelService;

	@Inject
	private ReportTemplateService reportTemplateService;

	@Inject 
	private ReportGenerationService reportGenService;
	
	@PostConstruct
	public void init() {
		chartTypeOptions = new LinkedHashMap<String, String>();
		
		chartTypeOptions.put(BAR.name(),"Bar Chart");
		chartTypeOptions.put(LINE.name(),"Line Chart");
		chartTypeOptions.put(AREA.name(),"Area Chart");
		chartTypeOptions.put(COLUMN.name(),"Column Chart");
		chartTypeOptions.put(PIE.name(),"Pie Chart");
		
		reportTemplate = new CartesianChartTemplate();
		reportTemplate.setReportTemplateType(ReportTemplateTypeEnum.SIMPLE);
	}
	
	/**
	 * re initialized pre request option
	 */
	public void initOptions(){
		reloadAvailableModel();
	}
	
	private void reloadAvailableModel(){
		
		availableModelOptions.clear();
		availableModelMap.clear();

		try {
			List<Model> availableModelList = modelService.findAll();
			
			for (Model model : availableModelList) {
				String key = String.valueOf(model.getId());
				availableModelOptions.put(key, model.getName());
				availableModelMap.put(key, model);
			}
		} 
		catch (ModelServiceException e) {
			LOG.warn("Failed loading available models", e);
		}
	}
	/**
	 * handle model change event
	 * @param event
	 */
	public void handleModelChange(AjaxBehaviorEvent event) {
		LOG.info("model changed "+selectedModel);
		
		model = availableModelMap.get(selectedModel);
		
		showModelDetails = (model!=null);
		
		if(model!=null){
			prepareFieldOptions(model);
			
			reportTemplate.setModelId(model.getId());
		}
	}
	
	/**
	 * 
	 * @param model
	 */
	private void prepareFieldOptions(Model model){
		dataValueFieldOptions.clear();
		modelFieldOptions.clear();
		
		for (AttributeMapping attMapping : model.getAttributeBindings()){
			
			SqlTypeEnum sqlType = SqlTypeEnum.fromString(attMapping.getTypeName());
			
			if(sqlType!=null){
				modelFieldOptions.put(attMapping.getAlias(), attMapping.getAlias());
				switch(sqlType){
					case BIGINT:
					case DECIMAL:
					case DOUBLE:
					case FLOAT:
					case INTEGER:
					case NUMERIC:
					case REAL:
					case SMALLINT:
					case TINYINT:
						dataValueFieldOptions.put(attMapping.getAlias(), attMapping.getAlias());
						break;
					default:
						break;
				}
			}
		}
	}
	
	/**
	 * 
	 * @param event
	 */
	public void handleChartTypeChange(AjaxBehaviorEvent event) {
		Object eventSource = event.getSource();
		
		if(eventSource instanceof HtmlSelectOneMenu){
			HtmlSelectOneMenu htmlSelectOneMenu = (HtmlSelectOneMenu) eventSource;
			ChartTypeEnum chartType = ChartTypeEnum.fromName(htmlSelectOneMenu.getValue().toString());
			
			selectedChartType = (chartType==null? "" : chartType.name());
			
			if(PIE.equals(chartType)){
				isPieChartType = true;
				
				handlePieChartTypeSwitch();
			}
			else{
				isPieChartType = false;
				handleCartesianChartTypeSwitch();
			}
			
			
	
			chartTypeImgSrc = "/images/"+chartType.name().toLowerCase()+"chart_small.png";
		}
	}
	
	/**
	 * 
	 */
	private void handleCartesianChartTypeSwitch(){
		//if current template is not pie chart type
		if(!(reportTemplate instanceof CartesianChartTemplate)){
			CartesianChartTemplate cartesianChartTemplate = new CartesianChartTemplate();

			populateChartTemplateAttributes((ChartTemplate)reportTemplate, cartesianChartTemplate);
			
			cartesianChartTemplate.setReportTemplateType(ReportTemplateTypeEnum.SIMPLE);			
			reportTemplate = cartesianChartTemplate;
		}
	}
	/**
	 * 
	 */
	private void handlePieChartTypeSwitch(){
		//if current template is not pie chart type
		if(!(reportTemplate instanceof PieChartTemplate)){
			PieChartTemplate pieChartTemplate = new PieChartTemplate();

			populateChartTemplateAttributes((ChartTemplate)reportTemplate, pieChartTemplate);
			pieChartTemplate.setReportTemplateType(ReportTemplateTypeEnum.SIMPLE);
			
			reportTemplate = pieChartTemplate;
		}
	}
	/**
	 * 
	 * @param actionEvent
	 */
	public void handleAddSeries(ActionEvent actionEvent) {
		TemplateSeries templateSeries = new TemplateSeries();
		templateSeries.setName("");
		templateSeries.setModelSeriesValue("");
		
		Set<TemplateSeries> templateSeriesSet = reportTemplate.getDataSeries();
		
		if(templateSeriesSet == null){
			templateSeriesSet = new LinkedHashSet<TemplateSeries>();
			reportTemplate.setDataSeries(templateSeriesSet);
		}
		
		templateSeriesSet.add(templateSeries);
	}
	
	/**
	 * 
	 * @param event
	 */
	public void handleSeriesGroupChange(AjaxBehaviorEvent event){
		
		String modelSeriesGroupField = ((SelectOneMenu) event.getSource()).getValue().toString();
		
		try {
			List<String> resultList = modelService.getModelFieldUniqueValue(model, modelSeriesGroupField);
			templateSeriesValueOptions.clear();

			for (String result : resultList) {
				templateSeriesValueOptions.put(result, result);
			}
		}
		catch(ModelServiceException e){
			LOG.warn("Failed to cobtain the unique value for series group selected ", e);
		}
	}
	/**
	 * 
	 * @param actionEvent
	 */
	public void handleRemoveSeries(ActionEvent actionEvent) {
		if (actionEvent.getSource() instanceof HtmlCommandLink) {
			HtmlCommandLink removeSeriesLink = (HtmlCommandLink) actionEvent.getSource();
			DataTable dataTable = (DataTable) removeSeriesLink.getParent().getParent();
			int currentIndex = dataTable.getRowIndex();
			
			Set<TemplateSeries> templateSeriesSet = reportTemplate.getDataSeries();
			
			//Note: due to the use of business key in hashcode(), business key updated before performing remove, so can't use Set.remove()
			if(templateSeriesSet!=null){
				
				Set<TemplateSeries> tempSeriesSet = new LinkedHashSet<TemplateSeries>();

				int idx = 0;

				for (TemplateSeries templateSeries : templateSeriesSet) {
					
					if(idx != currentIndex){
						tempSeriesSet.add(templateSeries);
					}
					idx++;
				}
				
				//clear existing
				reportTemplate.getDataSeries().clear();
				//add back the temp with removed entry
				reportTemplate.getDataSeries().addAll(tempSeriesSet);
			}
		}
	}
	
	/**
	 * 
	 */
	public void handleSaveOrUpdate(){
		
		ChartTypeEnum chartType = ChartTypeEnum.fromName(selectedChartType);
		
		try{
			if(reportTemplate.getId()>0){
				updateReportTemplate(chartType);
			}
			else{
				createReportTemplate(chartType);
			}
		}
		catch(ReportTemplateServiceException e){
			LOG.error("Failed save or update",e);
			//TODO: UI handling
		}
		//in even of error must close the dialog too
		finally{
			// Prepare data to pass it back to whatever that opened this dialog.
			Map<String, Object> data = new HashMap<String, Object>();
			//TODO: status
			
			//passed status back and close dialog
			RequestContext.getCurrentInstance().closeDialog(data);
		}
	}

	/**
	 * 
	 * @param chartType
	 * @throws ReportTemplateServiceException 
	 */
	private void updateReportTemplate(ChartTypeEnum chartType) throws ReportTemplateServiceException{
		
		if(reportTemplate instanceof AreaChartTemplate && AREA.equals(chartType)){
			reportTemplateService.update((AreaChartTemplate)reportTemplate);
		}
		else if(reportTemplate instanceof BarChartTemplate && BAR.equals(chartType)){
			reportTemplateService.update((BarChartTemplate)reportTemplate);
		}
		else if(reportTemplate instanceof ColumnChartTemplate && COLUMN.equals(chartType)){
			reportTemplateService.update((ColumnChartTemplate)reportTemplate);
		}
		else if(reportTemplate instanceof LineChartTemplate && LINE.equals(chartType)){
			reportTemplateService.update((LineChartTemplate)reportTemplate);
		}
		else if(reportTemplate instanceof PieChartTemplate && PIE.equals(chartType)){
			reportTemplateService.update((PieChartTemplate)reportTemplate);
		}
		else{
			//TODO: ui error handling
			LOG.error("invalid report template ");
		}
	}
	
	public void cancel() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}
	/**
	 * 
	 * @param chartType
	 * @throws ReportTemplateServiceException 
	 */
	private void createReportTemplate(ChartTypeEnum chartType) throws ReportTemplateServiceException{
		
		if(reportTemplate instanceof AreaChartTemplate && AREA.equals(chartType)){
			reportTemplateService.save((AreaChartTemplate)reportTemplate);
		}
		else if(reportTemplate instanceof BarChartTemplate && BAR.equals(chartType)){
			reportTemplateService.save((BarChartTemplate)reportTemplate);
		}
		else if(reportTemplate instanceof ColumnChartTemplate && COLUMN.equals(chartType)){
			reportTemplateService.save((ColumnChartTemplate)reportTemplate);
		}
		else if(reportTemplate instanceof LineChartTemplate && LINE.equals(chartType)){
			reportTemplateService.save((LineChartTemplate)reportTemplate);
		}
		else if(reportTemplate instanceof PieChartTemplate && PIE.equals(chartType)){
			reportTemplateService.save((PieChartTemplate)reportTemplate);
		}
		else{
			//TODO: ui error handling
			LOG.error("invalid report template ");
		}
	}
	
	/**
	 * due to ui design, if axis title not present, need to set the show Axis indicator accordingly
	 */
	private void updateShowAxisIndicator(){
		CartesianChartTemplate template = (CartesianChartTemplate)reportTemplate;
		
		template.setShowXAxis(StringUtils.isNotBlank(template.getXAxisTitle()));
		template.setShowYAxis(StringUtils.isNotBlank(template.getYAxisTitle()));
	}
	/**
	 * 
	 * @param source
	 * @param target
	 */
	private void populateCartesianChartTemplate(CartesianChartTemplate source, CartesianChartTemplate target){
		populateChartTemplateAttributes(source, target);
		
		target.setXAxisTitle(source.getXAxisTitle());
		target.setYAxisTitle(source.getYAxisTitle());
		target.setShowXAxis(source.isShowXAxis());
		target.setShowYAxis(source.isShowYAxis());
		target.setShowDataLabel(source.isShowDataLabel());
		target.setModelDataLabelField(source.getModelDataLabelField());
		target.setModelDataValueField(source.getModelDataValueField());
		target.setModelSeriesGroupField(source.getModelSeriesGroupField());
		target.setDataSeries(source.getDataSeries());
	}
	/**
	 * 
	 * @param source
	 * @param target
	 */
	private void populateChartTemplateAttributes(ChartTemplate source, ChartTemplate target){
		//populate common attributes
		populateBaseTemplateAttributes(source, target);
		
		target.setTitle(source.getTitle());
		target.setShowLegend(source.getShowLegend());
	}
	
	
	/**
	 * 
	 * @param source
	 * @param target
	 */
	private void populateBaseTemplateAttributes(BaseReportTemplate source, BaseReportTemplate target){
		target.setId(source.getId());
		target.setTemplateName(source.getTemplateName());
		
		target.setReportDisplayName(source.getReportDisplayName());
		target.setReportQuery(source.getReportQuery());
		target.setReportTemplateType(source.getReportTemplateType());
		
		target.setModelId(source.getModelId());
	}
	/**
	 * 
	 * @return
	 */
	public BaseReportTemplate getReportTemplate() {
		return reportTemplate;
	}

	/**
	 * 
	 * @param reportTemplate
	 */
	public void setReportTemplate(BaseReportTemplate reportTemplate) {
		this.reportTemplate = reportTemplate;
	}

	/**
	 * 
	 * @param event
	 * @return
	 */
	public String onFlowProcess(FlowEvent event) {
		//if transistion from tab "configuration" to "preview"
		if ("configuration".equals(event.getOldStep()) &&
			"preview".equals(event.getNewStep())) {
			generatePreview();
		}
		return event.getNewStep();
	}
	
	/**
	 * 
	 */
	private void generatePreview(){
		ChartTypeEnum chartType = ChartTypeEnum.fromName(selectedChartType);
		
		if(chartType!=null){
			
			if(reportTemplate instanceof CartesianChartTemplate){
				updateShowAxisIndicator();
				pfChartModel = generateCartesianChartPreview(chartType);
			}
			else if (reportTemplate instanceof PieChartTemplate && PIE.equals(chartType)){
				pfChartModel = generatePieChartPreview();
			}
			
			pfChartType = chartType.getCode();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private ChartModel generateCartesianChartPreview(ChartTypeEnum chartType){
		ChartModel chartModel = null;

		//0. backup templateId 
		int backupTemplateId = reportTemplate.getId(); 
		
		if(reportTemplate instanceof CartesianChartTemplate){
		
			switch(chartType){
				case AREA:
					chartModel = generateAreaChartPreview();
					break;
				case BAR:
					chartModel = generateBarChartPreview();
					break;
				case COLUMN:
					chartModel = generateColumnChartPreview();
					break;
				case LINE:
					chartModel = generateLineChartPreview();
					break;
				default:
					break;
			}
		}
		
		//restore the id
		reportTemplate.setId(backupTemplateId);
		
		
		return chartModel;
	}
	
	/**
	 * 
	 * @return
	 */
	private ChartModel generateAreaChartPreview(){
		ChartModel chartModel = null;
		
		AreaChartTemplate areaChartTemplate;

		try {
			//if template already is areaChartTemplate
			if(reportTemplate instanceof AreaChartTemplate){
				areaChartTemplate = (AreaChartTemplate)reportTemplate;
			}
			else{
				areaChartTemplate = new AreaChartTemplate();
				populateCartesianChartTemplate((CartesianChartTemplate)reportTemplate, areaChartTemplate);
				
				//assign the new AreaChartTemplate instance as current reference
				reportTemplate = areaChartTemplate;
			}
			
			//2. refine the model query to report query based on template setting 
			areaChartTemplate.setId(DUMMY_TEMPLATE_ID_FOR_QUERY_GEN);
			areaChartTemplate.setReportQuery(reportTemplateService.constructReportQuery(areaChartTemplate));
			
			//3. generate Pie report POJO
			AreaChartReport report = reportGenService.generateAreaChartReport(areaChartTemplate);
			
			if(report!=null){
				//4. convert to prime faces chart model
				chartModel = ReportUtil.mapReportToPFModel(report);
			}
		}
		catch (ReportTemplateServiceException e) {
			LOG.warn("failed constructing report query", e);
		} 
		catch (ReportGenerationServiceException e) {
			LOG.warn("failed generating report preview", e);
		}
		
		return chartModel;
	}
	
	/**
	 * 
	 * @return
	 */
	private ChartModel generateBarChartPreview(){
		ChartModel chartModel = null;
		
		BarChartTemplate barChartTemplate;

		try {
			//if template already is BarChartTemplate
			if(reportTemplate instanceof BarChartTemplate){
				barChartTemplate = (BarChartTemplate)reportTemplate;
			}
			else{
				barChartTemplate = new BarChartTemplate();
				populateCartesianChartTemplate((CartesianChartTemplate)reportTemplate, barChartTemplate);
				
				//assign the new BarChartTemplate instance as current reference
				reportTemplate = barChartTemplate;
			}
			
			//2. refine the model query to report query based on template setting 
			barChartTemplate.setId(DUMMY_TEMPLATE_ID_FOR_QUERY_GEN);
			barChartTemplate.setReportQuery(reportTemplateService.constructReportQuery(barChartTemplate));
			
			//3. generate Pie report POJO
			BarChartReport report = reportGenService.generateBarChartReport(barChartTemplate);
			
			if(report!=null){
				//4. convert to prime faces chart model
				chartModel = ReportUtil.mapReportToPFModel(report);
			}
		}
		catch (ReportTemplateServiceException e) {
			LOG.warn("failed constructing report query", e);
		} 
		catch (ReportGenerationServiceException e) {
			LOG.warn("failed generating report preview", e);
		}
		
		return chartModel;
	}
	/**
	 * 
	 * @return
	 */
	private ChartModel generateColumnChartPreview(){
		ChartModel chartModel = null;
		
		ColumnChartTemplate columnChartTemplate;

		try {
			//if template already is ColumnChartTemplate
			if(reportTemplate instanceof ColumnChartTemplate){
				columnChartTemplate = (ColumnChartTemplate)reportTemplate;
			}
			else{
				columnChartTemplate = new ColumnChartTemplate();
				populateCartesianChartTemplate((CartesianChartTemplate)reportTemplate, columnChartTemplate);
				
				//assign the new ColumnChartTemplate instance as current reference
				reportTemplate = columnChartTemplate;
			}
			
			//2. refine the model query to report query based on template setting 
			columnChartTemplate.setId(DUMMY_TEMPLATE_ID_FOR_QUERY_GEN);
			columnChartTemplate.setReportQuery(reportTemplateService.constructReportQuery(columnChartTemplate));
			
			//3. generate Pie report POJO
			ColumnChartReport report = reportGenService.generateColumnChartReport(columnChartTemplate);
			
			if(report!=null){
				//4. convert to prime faces chart model
				chartModel = ReportUtil.mapReportToPFModel(report);
			}
		}
		catch (ReportTemplateServiceException e) {
			LOG.warn("failed constructing report query", e);
		} 
		catch (ReportGenerationServiceException e) {
			LOG.warn("failed generating report preview", e);
		}
		
		return chartModel;
	}
	
	/**
	 * 
	 * @return
	 */
	private ChartModel generateLineChartPreview(){
		ChartModel chartModel = null;
		
		LineChartTemplate lineChartTemplate;

		try {
			//if template already is LineChartTemplate
			if(reportTemplate instanceof LineChartTemplate){
				lineChartTemplate = (LineChartTemplate)reportTemplate;
			}
			else{
				lineChartTemplate = new LineChartTemplate();
				populateCartesianChartTemplate((CartesianChartTemplate)reportTemplate, lineChartTemplate);
				
				//assign the new LineChartTemplate instance as current reference
				reportTemplate = lineChartTemplate;
			}
			
			//2. refine the model query to report query based on template setting 
			lineChartTemplate.setId(DUMMY_TEMPLATE_ID_FOR_QUERY_GEN);
			lineChartTemplate.setReportQuery(reportTemplateService.constructReportQuery(lineChartTemplate));
			
			//3. generate Pie report POJO
			LineChartReport report = reportGenService.generateLineChartReport(lineChartTemplate);
			
			if(report!=null){
				//4. convert to prime faces chart model
				chartModel = ReportUtil.mapReportToPFModel(report);
			}
		}
		catch (ReportTemplateServiceException e) {
			LOG.warn("failed constructing report query", e);
		} 
		catch (ReportGenerationServiceException e) {
			LOG.warn("failed generating report preview", e);
		}
		
		return chartModel;
	}
	
	/**
	 * 
	 * @return
	 */
	private ChartModel generatePieChartPreview(){
		
		ChartModel chartModel = null;

		//0. backup templateId 
		int backupTemplateId = reportTemplate.getId(); 

		try {
			//1. cast to specific template
			PieChartTemplate pieChartTemplate = (PieChartTemplate)reportTemplate;
			
			//2. refine the model query to report query based on template setting 
			pieChartTemplate.setId(DUMMY_TEMPLATE_ID_FOR_QUERY_GEN);
			pieChartTemplate.setReportQuery(reportTemplateService.constructReportQuery(pieChartTemplate));
			
			//3. generate Pie report POJO
			PieChartReport report = reportGenService.generatePieChartReport(pieChartTemplate);
			
			if(report!=null){
				//4. convert to prime faces chart model
				chartModel = ReportUtil.mapReportToPFModel(report);
			}
		} 
		catch (ReportTemplateServiceException e) {
			LOG.warn("failed constructing report query", e);
		} 
		catch (ReportGenerationServiceException e) {
			LOG.warn("failed generating report preview", e);
		}
		finally{
			//restored back up templateId
			reportTemplate.setId(backupTemplateId);
		}
		
		return chartModel;
	}

	/**
	 * @return the selectedChartType
	 */
	public String getSelectedChartType() {
		return selectedChartType;
	}

	/**
	 * @param selectedChartType the selectedChartType to set
	 */
	public void setSelectedChartType(String selectedChartType) {
		this.selectedChartType = selectedChartType;
	}

	/**
	 * @return the chartTypesOptions
	 */
	public Map<String, String> getChartTypeOptions() {
		return chartTypeOptions;
	}

	/**
	 * @param chartTypesOptions the chartTypesOptions to set
	 */
	public void setChartTypeOptions(Map<String, String> chartTypeOptions) {
		this.chartTypeOptions = chartTypeOptions;
	}
	/**
	 * @return the selectedModel
	 */
	public String getSelectedModel() {
		return selectedModel;
	}
	/**
	 * @param selectedModel the selectedModel to set
	 */
	public void setSelectedModel(String selectedModel) {
		this.selectedModel = selectedModel;
	}
	/**
	 * @return the availableModelOptions
	 */
	public Map<String, String> getAvailableModelOptions() {
		return availableModelOptions;
	}
	/**
	 * @param availableModelOptions the availableModelOptions to set
	 */
	public void setAvailableModelOptions(Map<String, String> availableModelOptions) {
		this.availableModelOptions = availableModelOptions;
	}

	/**
	 * @return the showModelDetails
	 */
	public boolean isShowModelDetails() {
		return showModelDetails;
	}

	/**
	 * @param showModelDetails the showModelDetails to set
	 */
	public void setShowModelDetails(boolean showModelDetails) {
		this.showModelDetails = showModelDetails;
	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * @return the isPieChartType
	 */
	public boolean isPieChartType() {
		
		return isPieChartType;
	}

	/**
	 * @param isPieChartType the isPieChartType to set
	 */
	public void setPieChartType(boolean isPieChartType) {
		this.isPieChartType = isPieChartType;
	}

	/**
	 * @return the chartTypeImgSrc
	 */
	public String getChartTypeImgSrc() {
		return chartTypeImgSrc;
	}

	/**
	 * @param chartTypeImgSrc the chartTypeImgSrc to set
	 */
	public void setChartTypeImgSrc(String chartTypeImgSrc) {
		this.chartTypeImgSrc = chartTypeImgSrc;
	}

	/**
	 * @return the dataValueFieldOptions
	 */
	public Map<String, String> getDataValueFieldOptions() {
		return dataValueFieldOptions;
	}

	/**
	 * @param dataValueFieldOptions the dataValueFieldOptions to set
	 */
	public void setDataValueFieldOptions(Map<String, String> dataValueFieldOptions) {
		this.dataValueFieldOptions = dataValueFieldOptions;
	}

	/**
	 * @return the modelFieldOptions
	 */
	public Map<String, String> getModelFieldOptions() {
		return modelFieldOptions;
	}

	/**
	 * @param modelFieldOptions the modelFieldOptions to set
	 */
	public void setModelFieldOptions(Map<String, String> modelFieldOptions) {
		this.modelFieldOptions = modelFieldOptions;
	}
	
	public Map<String, String> getCategoryFieldOptions() {
		return getModelFieldOptions();
	}

	/**
	 * @return the showLegend
	 */
	public boolean isShowLegend() {
		return showLegend;
	}

	/**
	 * @param showLegend the showLegend to set
	 */
	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
		
		if(reportTemplate instanceof ChartTemplate){
			((ChartTemplate)reportTemplate).setShowLegend(showLegend);
		}
	}

	/**
	 * @return the showDataLabel
	 */
	public boolean isShowDataLabel() {
		return showDataLabel;
	}

	/**
	 * @param showDataLabel the showDataLabel to set
	 */
	public void setShowDataLabel(boolean showDataLabel) {
		this.showDataLabel = showDataLabel;
		
		if(reportTemplate instanceof PieChartTemplate){
			((PieChartTemplate) reportTemplate).setShowDataLabel(showDataLabel);
		}
	}

	/**
	 * @return the pfChartModel
	 */
	public ChartModel getPfChartModel() {
		return pfChartModel;
	}

	/**
	 * @param pfChartModel the pfChartModel to set
	 */
	public void setPfChartModel(ChartModel pfChartModel) {
		this.pfChartModel = pfChartModel;
	}

	/**
	 * @return the pfChartType
	 */
	public String getPfChartType() {
		return pfChartType;
	}

	/**
	 * @param pfChartType the pfChartType to set
	 */
	public void setPfChartType(String pfChartType) {
		this.pfChartType = pfChartType;
	}

	/**
	 * @return the templateSeriesValueOptions
	 */
	public Map<String, String> getTemplateSeriesValueOptions() {
		return templateSeriesValueOptions;
	}

	/**
	 * @param templateSeriesValueOptions the templateSeriesValueOptions to set
	 */
	public void setTemplateSeriesValueOptions(Map<String, String> templateSeriesValueOptions) {
		this.templateSeriesValueOptions = templateSeriesValueOptions;
	}
}