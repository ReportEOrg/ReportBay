package org.reporte.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
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
import org.reporte.reporttemplate.domain.ColumnChartTemplate;
import org.reporte.reporttemplate.domain.LineChartTemplate;
import org.reporte.reporttemplate.domain.PieChartTemplate;
import org.reporte.reporttemplate.domain.ReportTemplateTypeEnum;
import org.reporte.reporttemplate.domain.TemplateSeries;
import org.reporte.reporttemplate.service.ReportTemplateService;
import org.reporte.reporttemplate.service.exception.ReportTemplateServiceException;
import org.reporte.web.common.ChartTypeEnum;
import org.reporte.web.util.ReportUtil;

/**
 * Report Template JSF Backing bean
 *
 */
@Named("reportTemplate")
@SessionScoped
public class ReportTemplateBean implements Serializable {
	private static final long serialVersionUID = 696889640817167123L;
	private static final Logger LOG = Logger.getLogger(ReportTemplateBean.class);

	private String templateName;
	private List<String> chartTypes;
	private String selectedChartType;
	private String chartTitle;
	private String chartTypeImgSrc;
	private String reportDisplayName;
	private int chartTemplateId;

	private String selectedModelName;
	private Model selectedModel;
	private List<String> existingModelNames;
	List<Model> allModels;
	private String selectedModelDescription;
	List<AttributeMapping> modelAttrs;
	List<AttributeMapping> onlyIntegerModelAttrs = new ArrayList<AttributeMapping>();
	private boolean showModelDetails = false;

	private boolean showLegend;
	private String xAxisTitle;
	private String yAxisTitle;
	private boolean showDataLabel;
	private String modelDataLabelField;
	private String modelDataValueField;
	private String modelSeriesGroupField;
	private String mandatoryTemplateSeriesVlaue;

	private String pieDataField;
	private String pieCatagoryField;
	private boolean pieChart;
	private String chartType;

	private ChartModel chartModel;
	private boolean edit;

	private List<TemplateSeries> templateSeriesValues;
	private String saveOrUpdate;

	private List<BaseReportTemplate> orgExtReportTemplateList = new ArrayList<BaseReportTemplate>();
	private List<BaseReportTemplate> extReportTemplateList = new ArrayList<BaseReportTemplate>();

	private BaseReportTemplate currentDisplayTemplate;

	@Inject
	private ModelService modelService;

	@Inject
	private ReportGenerationService reportGenService;

	@Inject
	private ReportTemplateService reportTemplateService;

	private int tabActiveIndex;

	@PostConstruct
	public void init() {
		resetTemplateData();
		loadReportTemplates();
	}

	public void resetTemplateData() {
		templateName = "";
		selectedModel = null;
		selectedModelName = "";
		selectedModelDescription = "";
		modelAttrs = null;
		onlyIntegerModelAttrs.clear();
		reportDisplayName = "";
		chartTitle = "";
		showLegend = false;
		showDataLabel = false;
		pieCatagoryField = "";
		pieDataField = "";

		xAxisTitle = "";
		yAxisTitle = "";
		modelDataLabelField = "";
		modelDataValueField = "";
		modelSeriesGroupField = "";
		templateSeriesValues = new ArrayList<TemplateSeries>();

		chartTemplateId = 0;
		selectedChartType = "";
		chartTypeImgSrc = "";
		showModelDetails = false;

		chartTypes = new ArrayList<String>();
		chartTypes.add("Select Chart Type");
		chartTypes.add("Bar Chart");
		chartTypes.add("Line Chart");
		chartTypes.add("Area Chart");
		chartTypes.add("Column Chart");
		chartTypes.add("Pie Chart");

		pieChart = false;
		chartType = "";
		edit = false;
		saveOrUpdate = "Save";
		chartModel = null;
		loadExistingModels();
	}

	public void createNewReportTemplate() {
		resetTemplateData();
		Map<String, String> params = new HashMap<String, String>();
		params.put("title", "New Report Template");
		openNewReportTemplateDialog(params);
	}

	public void loadExistingModels() {
		try {
			allModels = modelService.findAll();
			existingModelNames = new ArrayList<String>();
			existingModelNames.add("Select Model");
			if (allModels != null) {
				for (Model model : allModels) {
					existingModelNames.add(model.getName());
				}
			}
		} catch (ModelServiceException e) {
			LOG.error("Failed to load all existing Models.", e);
		}
	}

	public String loadReportTemplates() {
		resetTemplateData();
		loadReportTemplateList();
		return "/views/template/manage_templates.xhtml?faces-redirect=true";
	}

	private void loadReportTemplateList() {
		try {
			boolean initLoaded = false;
			orgExtReportTemplateList.clear();
			orgExtReportTemplateList.addAll(reportTemplateService.findAllReportTemplate());
			extReportTemplateList.clear();
			if (orgExtReportTemplateList != null && orgExtReportTemplateList.size() > 0) {
				for (BaseReportTemplate baseReportTemplate : orgExtReportTemplateList) {
					extReportTemplateList.add(baseReportTemplate);
					if (!initLoaded) {
						currentDisplayTemplate = baseReportTemplate;
						if (baseReportTemplate instanceof PieChartTemplate) {
							pieChart = true;
							loadPieChartTemplate((PieChartTemplate) baseReportTemplate);
						} else if (baseReportTemplate instanceof CartesianChartTemplate) {
							pieChart = false;
							loadCartesianChartTemplate((CartesianChartTemplate) baseReportTemplate);
						}
						initLoaded = true;
					}
				}
				tabActiveIndex = 0;
			}
		} catch (ReportTemplateServiceException rtse) {
			LOG.error("failed to find all report template");
		}
	}

	public void editReportTemplate() {
		if (currentDisplayTemplate instanceof PieChartTemplate) {
			pieChart = true;
			loadPieChartTemplate((PieChartTemplate) currentDisplayTemplate);
		} else if (currentDisplayTemplate instanceof CartesianChartTemplate) {
			pieChart = false;
			loadCartesianChartTemplate((CartesianChartTemplate) currentDisplayTemplate);
		}
		edit = true;
		saveOrUpdate = "Update";
		Map<String, String> params = new HashMap<String, String>();
		params.put("title", "Edit Report Template");
		openNewReportTemplateDialog(params);
	}

	private void openNewReportTemplateDialog(Map<String, String> params) {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("draggable", false);
		options.put("resizable", true);
		options.put("contentWidth", 1000);
		options.put("contentHeight", 550);
		Map<String, List<String>> requestParams = new HashMap<String, List<String>>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			List<String> values = new ArrayList<String>();
			values.add(entry.getValue());
			requestParams.put(entry.getKey(), values);
		}
		RequestContext.getCurrentInstance().openDialog("create_report_template", options, requestParams);
	}

	public void cancel() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}

	public void chartTypeChanged(AjaxBehaviorEvent event) {
		if (event.getSource() instanceof HtmlSelectOneMenu) {
			HtmlSelectOneMenu htmlSelectOneMenu = (HtmlSelectOneMenu) event.getSource();
			pieChart = false;
			if ("Bar Chart".equalsIgnoreCase(htmlSelectOneMenu.getValue().toString())) {
				chartTypeImgSrc = "/images/barchart_small.png";
			} else if ("Line Chart".equalsIgnoreCase(htmlSelectOneMenu.getValue().toString())) {
				chartTypeImgSrc = "/images/linechart_small.png";
			} else if ("Area Chart".equalsIgnoreCase(htmlSelectOneMenu.getValue().toString())) {
				chartTypeImgSrc = "/images/areachart_small.png";
			} else if ("Pie Chart".equalsIgnoreCase(htmlSelectOneMenu.getValue().toString())) {
				chartTypeImgSrc = "/images/piechart_small.png";
				pieChart = true;
			} else {
				chartTypeImgSrc = "";
			}
		}
	}

	public void modelChanged(AjaxBehaviorEvent event) {
		modelAttrs = null;
		onlyIntegerModelAttrs.clear();
		pieCatagoryField = "";
		pieDataField = "";
		xAxisTitle = "";
		yAxisTitle = "";
		modelDataLabelField = "";
		modelDataValueField = "";
		modelSeriesGroupField = "";
		templateSeriesValues = new ArrayList<TemplateSeries>();

		if (selectedModelName != null && selectedModelName.trim().length() > 0 && !selectedModelName.equals("Select Model")) {
			for (Model model : allModels) {
				if (model.getName().equals(selectedModelName)) {
					modelAttrs = model.getAttributeBindings();
					addOnlyIntegerAttributes(modelAttrs);
					showModelDetails = true;
					selectedModelDescription = model.getDescription();
					selectedModel = model;
					return;
				}
			}
		}
		if (selectedModelName.equals("Select Model")) {
			showModelDetails = false;
		}
	}

	public void addOnlyIntegerAttributes(List<AttributeMapping> attributeMappings) {
		onlyIntegerModelAttrs.clear();
		for (AttributeMapping attMapping : attributeMappings) {
			SqlTypeEnum sqlType = SqlTypeEnum.fromString(attMapping.getTypeName());
			if (SqlTypeEnum.BIGINT.equals(sqlType) || SqlTypeEnum.DECIMAL.equals(sqlType) || SqlTypeEnum.NUMERIC.equals(sqlType) || SqlTypeEnum.DOUBLE.equals(sqlType)
					|| SqlTypeEnum.FLOAT.equals(sqlType) || SqlTypeEnum.INTEGER.equals(sqlType) || SqlTypeEnum.REAL.equals(sqlType) || SqlTypeEnum.SMALLINT.equals(sqlType)
					|| SqlTypeEnum.TINYINT.equals(sqlType)) {
				onlyIntegerModelAttrs.add(attMapping);
			}
		}
	}

	public void addNewSeriesValue(ActionEvent actionEvent) {
		TemplateSeries templateSeries = new TemplateSeries();
		templateSeries.setName("");
		templateSeries.setModelSeriesValue("");
		templateSeriesValues.add(templateSeries);
	}

	public void removeSeriesValue(ActionEvent actionEvent) {
		if (actionEvent.getSource() instanceof HtmlCommandLink) {
			HtmlCommandLink removeSeriesLink = (HtmlCommandLink) actionEvent.getSource();
			DataTable dataTable = (DataTable) removeSeriesLink.getParent().getParent();
			int currentIndex = dataTable.getRowIndex();
			if (templateSeriesValues.size() >= currentIndex) {
				templateSeriesValues.remove(currentIndex);
			}
		}
	}

	public String onFlowProcess(FlowEvent event) {
		if (event.getNewStep().equals("preview") && event.getOldStep().equals("setup")) {
			if ("Bar Chart".equalsIgnoreCase(selectedChartType)) {
				generateBarChartModel(null);
			} else if ("Line Chart".equalsIgnoreCase(selectedChartType)) {
				generateLineChartModel(null);
			} else if ("Area Chart".equalsIgnoreCase(selectedChartType)) {
				generateAreaChartModel(null);
			} else if ("Column Chart".equalsIgnoreCase(selectedChartType)) {
				generateColumnChartModel(null);
			} else if ("Pie Chart".equalsIgnoreCase(selectedChartType)) {
				generatePieChartModel(null);
			}
		}
		return event.getNewStep();
	}

	public void onReportTemplateChange(TabChangeEvent event) {
		currentDisplayTemplate = (BaseReportTemplate) event.getData();
		if (event.getData() instanceof PieChartTemplate) {
			pieChart = true;
			loadPieChartTemplate((PieChartTemplate) event.getData());
		} else if ((event.getData() instanceof CartesianChartTemplate)) {
			pieChart = false;
			loadCartesianChartTemplate((CartesianChartTemplate) event.getData());
		}
	}

	public void deleteReportTemplate() {
		try {
			if (currentDisplayTemplate instanceof BarChartTemplate) {
				reportTemplateService.delete((BarChartTemplate) currentDisplayTemplate);
			} else if (currentDisplayTemplate instanceof LineChartTemplate) {
				reportTemplateService.delete((LineChartTemplate) currentDisplayTemplate);
			} else if (currentDisplayTemplate instanceof AreaChartTemplate) {
				reportTemplateService.delete((AreaChartTemplate) currentDisplayTemplate);
			} else if (currentDisplayTemplate instanceof ColumnChartTemplate) {
				reportTemplateService.delete((ColumnChartTemplate) currentDisplayTemplate);
			} else if (currentDisplayTemplate instanceof PieChartTemplate) {
				reportTemplateService.delete((PieChartTemplate) currentDisplayTemplate);
			}
		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}
		resetTemplateData();
		loadReportTemplateList();
	}

	public void filterExtReportTemplates(AjaxBehaviorEvent event) {
		if (event.getSource() instanceof HtmlInputText) {
			String enteredValue = ((HtmlInputText) event.getSource()).getValue().toString();
			extReportTemplateList.clear();
			for (BaseReportTemplate baseReportTemplate : orgExtReportTemplateList) {
				if (baseReportTemplate.getReportDisplayName().toUpperCase().contains(enteredValue.toUpperCase())) {
					extReportTemplateList.add(baseReportTemplate);
				}
			}
		}
	}

	private void fillCartesianChartTemplateForPreview(CartesianChartTemplate cartesianChartTemplate) {
		cartesianChartTemplate.setTemplateName(templateName);
		cartesianChartTemplate.setModelId(Integer.parseInt(new Integer(selectedModel.getId()).toString()));
		cartesianChartTemplate.setReportTemplateType(ReportTemplateTypeEnum.SIMPLE);
		cartesianChartTemplate.setReportDisplayName(reportDisplayName);
		cartesianChartTemplate.setTitle(chartTitle);
		cartesianChartTemplate.setShowLegend(showLegend);
		boolean xAxisInd = (xAxisTitle == null || xAxisTitle.trim().length() == 0) ? false : true;
		cartesianChartTemplate.setShowXAxis(xAxisInd);
		if (xAxisInd) {
			cartesianChartTemplate.setXAxisTitle(xAxisTitle);
		}
		boolean yAxisInd = (yAxisTitle == null || yAxisTitle.trim().length() == 0) ? false : true;
		cartesianChartTemplate.setShowYAxis(yAxisInd);
		if (yAxisInd) {
			cartesianChartTemplate.setYAxisTitle(yAxisTitle);
		}
		cartesianChartTemplate.setShowDataLabel(showDataLabel);
		cartesianChartTemplate.setModelDataLabelField(modelDataLabelField);
		cartesianChartTemplate.setModelDataValueField(modelDataValueField);
		cartesianChartTemplate.setModelSeriesGroupField(modelSeriesGroupField);

		Set<TemplateSeries> templateSeries = new LinkedHashSet<TemplateSeries>();
		for (TemplateSeries series : templateSeriesValues) {
			templateSeries.add(series);
		}
		cartesianChartTemplate.setDataSeries(templateSeries);

		// Setting only for Preview.
		cartesianChartTemplate.setId(123);
	}

	private BarChartTemplate generateBarChartTemplate() {
		BarChartTemplate barChartTemplate = new BarChartTemplate();
		fillCartesianChartTemplateForPreview(barChartTemplate);
		try {
			barChartTemplate.setReportQuery(reportTemplateService.constructReportQuery(barChartTemplate));
		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}
		return barChartTemplate;
	}

	private void loadPieChartTemplate(PieChartTemplate pieChartTemplate) {
		templateName = pieChartTemplate.getTemplateName();
		try {
			selectedModel = modelService.find(pieChartTemplate.getModelId());
			selectedModelName = selectedModel.getName();
			selectedModelDescription = selectedModel.getDescription();
			modelAttrs = selectedModel.getAttributeBindings();
			addOnlyIntegerAttributes(modelAttrs);
			showModelDetails = true;
		} catch (ModelServiceException e1) {
			e1.printStackTrace();
		}
		reportDisplayName = pieChartTemplate.getReportDisplayName();
		chartTitle = pieChartTemplate.getTitle();
		showLegend = pieChartTemplate.getShowLegend();
		showDataLabel = pieChartTemplate.getShowDataLabel();
		try {
			pieChartTemplate.setReportQuery(reportTemplateService.findReportQuery(pieChartTemplate.getId()));

		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}

		pieCatagoryField = pieChartTemplate.getModelCategoryField();
		pieDataField = pieChartTemplate.getModelDataField();
		chartTemplateId = pieChartTemplate.getId();

		generatePieChartModel(pieChartTemplate);
		selectedChartType = "Pie Chart";
		chartTypeImgSrc = "/images/piechart_small.png";
	}

	private void loadCartesianChartTemplate(CartesianChartTemplate cartesianChartTemplate) {
		templateName = cartesianChartTemplate.getTemplateName();
		try {
			selectedModel = modelService.find(cartesianChartTemplate.getModelId());
			selectedModelName = selectedModel.getName();
			selectedModelDescription = selectedModel.getDescription();
			modelAttrs = selectedModel.getAttributeBindings();
			addOnlyIntegerAttributes(modelAttrs);
			showModelDetails = true;
		} catch (ModelServiceException e1) {
			e1.printStackTrace();
		}
		reportDisplayName = cartesianChartTemplate.getReportDisplayName();
		chartTitle = cartesianChartTemplate.getTitle();
		showLegend = cartesianChartTemplate.getShowLegend();
		showDataLabel = cartesianChartTemplate.getShowDataLabel();
		try {
			cartesianChartTemplate.setReportQuery(reportTemplateService.findReportQuery(cartesianChartTemplate.getId()));

		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}

		xAxisTitle = cartesianChartTemplate.getXAxisTitle();
		yAxisTitle = cartesianChartTemplate.getYAxisTitle();
		modelDataLabelField = cartesianChartTemplate.getModelDataLabelField();
		modelDataValueField = cartesianChartTemplate.getModelDataValueField();
		modelSeriesGroupField = cartesianChartTemplate.getModelSeriesGroupField();
		Set<TemplateSeries> templateSeries = cartesianChartTemplate.getDataSeries();
		templateSeriesValues = new ArrayList<TemplateSeries>();
		for (TemplateSeries series : templateSeries) {
			templateSeriesValues.add(series);
		}
		cartesianChartTemplate.setModelId(Integer.parseInt(new Integer(selectedModel.getId()).toString()));
		cartesianChartTemplate.setReportTemplateType(ReportTemplateTypeEnum.SIMPLE);

		new LinkedHashSet<TemplateSeries>();
		for (TemplateSeries series : templateSeriesValues) {
			templateSeries.add(series);
		}
		cartesianChartTemplate.setDataSeries(templateSeries);
		chartTemplateId = cartesianChartTemplate.getId();

		if (cartesianChartTemplate instanceof BarChartTemplate) {
			generateBarChartModel((BarChartTemplate) cartesianChartTemplate);
			selectedChartType = "Bar Chart";
			chartTypeImgSrc = "/images/barchart_small.png";
		} else if (cartesianChartTemplate instanceof LineChartTemplate) {
			selectedChartType = "Line Chart";
			generateLineChartModel((LineChartTemplate) cartesianChartTemplate);
			chartTypeImgSrc = "/images/linechart_small.png";
		} else if (cartesianChartTemplate instanceof AreaChartTemplate) {
			generateAreaChartModel((AreaChartTemplate) cartesianChartTemplate);
			selectedChartType = "Area Chart";
			chartTypeImgSrc = "/images/areachart_small.png";
		} else if (cartesianChartTemplate instanceof ColumnChartTemplate) {
			generateColumnChartModel((ColumnChartTemplate) cartesianChartTemplate);
			selectedChartType = "Column Chart";
		}
		pieChart = false;
	}

	public void generateBarChartModel(BarChartTemplate barchartTemplate) {
		if (barchartTemplate == null) {
			barchartTemplate = generateBarChartTemplate();
		}
		try {
			BarChartReport report = reportGenService.generateBarChartReport(barchartTemplate);
			chartModel = ReportUtil.mapReportToPFModel(report);
			chartType = ChartTypeEnum.BAR.getCode();
		} catch (ReportGenerationServiceException e) {
			e.printStackTrace();
		}

	}

	public void saveOrUpdateBarchart() {
		BarChartTemplate barChartTemplate = generateBarChartTemplate();
		barChartTemplate.setId(chartTemplateId);
		try {
			if (edit) {
				reportTemplateService.update(barChartTemplate);
			} else {
				reportTemplateService.save(barChartTemplate);
			}
		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}
	}

	private AreaChartTemplate generateAreaChartTemplate() {
		AreaChartTemplate areaChartTemplate = new AreaChartTemplate();
		fillCartesianChartTemplateForPreview(areaChartTemplate);

		try {
			areaChartTemplate.setReportQuery(reportTemplateService.constructReportQuery(areaChartTemplate));
		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}
		return areaChartTemplate;
	}

	private void generateAreaChartModel(AreaChartTemplate areaChartTemplate) {
		if (areaChartTemplate == null) {
			areaChartTemplate = generateAreaChartTemplate();
		}
		try {
			AreaChartReport report = reportGenService.generateAreaChartReport(areaChartTemplate);
			chartModel = ReportUtil.mapReportToPFModel(report);
			chartType = ChartTypeEnum.AREA.getCode();
		} catch (ReportGenerationServiceException e) {
			e.printStackTrace();
		}
	}

	public void saveOrUpdateAreaChart() {
		AreaChartTemplate areaChartTemplate = generateAreaChartTemplate();
		areaChartTemplate.setId(chartTemplateId);
		try {
			if (edit) {
				reportTemplateService.update(areaChartTemplate);
			} else {
				reportTemplateService.save(areaChartTemplate);
			}
		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}
	}

	private ColumnChartTemplate generateColumnChartTemplate() {
		ColumnChartTemplate columnChartTemplate = new ColumnChartTemplate();
		fillCartesianChartTemplateForPreview(columnChartTemplate);
		try {
			columnChartTemplate.setReportQuery(reportTemplateService.constructReportQuery(columnChartTemplate));
		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}
		return columnChartTemplate;
	}

	public void generateColumnChartModel(ColumnChartTemplate columnChartTemplate) {
		if (columnChartTemplate == null) {
			columnChartTemplate = generateColumnChartTemplate();
		}
		try {
			ColumnChartReport report = reportGenService.generateColumnChartReport(columnChartTemplate);
			chartModel = ReportUtil.mapReportToPFModel(report);
			chartType = ChartTypeEnum.COLUMN.getCode();
		} catch (ReportGenerationServiceException e) {
			e.printStackTrace();
		}
	}

	public void saveOrUpdateColumnChart() {
		ColumnChartTemplate columnChartTemplate = generateColumnChartTemplate();
		columnChartTemplate.setId(chartTemplateId);
		try {
			if (edit) {
				reportTemplateService.update(columnChartTemplate);
			} else {
				reportTemplateService.save(columnChartTemplate);
			}
		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}
	}

	private LineChartTemplate generateLineChartTemplate() {
		LineChartTemplate lineChartTemplate = new LineChartTemplate();
		fillCartesianChartTemplateForPreview(lineChartTemplate);
		try {
			lineChartTemplate.setReportQuery(reportTemplateService.constructReportQuery(lineChartTemplate));
		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}
		return lineChartTemplate;
	}

	public void generateLineChartModel(LineChartTemplate lineChartTemplate) {
		if (lineChartTemplate == null) {
			lineChartTemplate = generateLineChartTemplate();
		}
		try {
			LineChartReport report = reportGenService.generateLineChartReport(lineChartTemplate);
			chartModel = ReportUtil.mapReportToPFModel(report);
			chartType = ChartTypeEnum.LINE.getCode();
		} catch (ReportGenerationServiceException e) {
			e.printStackTrace();
		}

	}

	public void saveOrUpdateLineChart() {
		LineChartTemplate lineChartTemplate = generateLineChartTemplate();
		lineChartTemplate.setId(chartTemplateId);
		try {
			if (edit) {
				reportTemplateService.update(lineChartTemplate);
			} else {
				reportTemplateService.save(lineChartTemplate);
			}
		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}
	}

	private PieChartTemplate generatePieChartTemplate() {
		PieChartTemplate pieChartTemplate = getPieChartTemplate();
		try {
			pieChartTemplate.setReportQuery(reportTemplateService.constructReportQuery(pieChartTemplate));
		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}
		return pieChartTemplate;
	}

	public void generatePieChartModel(PieChartTemplate pieChartTemplate) {
		if (pieChartTemplate == null) {
			pieChartTemplate = generatePieChartTemplate();
		}
		try {
			PieChartReport report = reportGenService.generatePieChartReport(pieChartTemplate);
			chartType = ChartTypeEnum.PIE.getCode();
			chartModel = ReportUtil.mapReportToPFModel(report);
		} catch (ReportGenerationServiceException e) {
			e.printStackTrace();
		}
	}

	public void saveOrUpdatePieChart() {
		PieChartTemplate pieChartTemplate = generatePieChartTemplate();
		pieChartTemplate.setId(chartTemplateId);
		try {
			if (edit) {
				reportTemplateService.update(pieChartTemplate);
			} else {
				reportTemplateService.save(pieChartTemplate);
			}
		} catch (ReportTemplateServiceException e) {
			e.printStackTrace();
		}
	}

	private PieChartTemplate getPieChartTemplate() {
		PieChartTemplate pieChartTemplate = new PieChartTemplate();
		pieChartTemplate.setTemplateName(templateName);
		pieChartTemplate.setModelId(Integer.parseInt(new Integer(selectedModel.getId()).toString()));
		pieChartTemplate.setReportTemplateType(ReportTemplateTypeEnum.SIMPLE);
		pieChartTemplate.setTitle(chartTitle);
		pieChartTemplate.setShowLegend(showLegend);
		pieChartTemplate.setShowDataLabel(showDataLabel);
		pieChartTemplate.setModelCategoryField(pieCatagoryField);
		pieChartTemplate.setModelDataField(pieDataField);
		pieChartTemplate.setReportDisplayName(reportDisplayName);
		// Setting only for Preview.
		pieChartTemplate.setId(123);
		return pieChartTemplate;
	}

	public void saveOrUpdateReportTemplate() {
		if ("Bar Chart".equalsIgnoreCase(selectedChartType)) {
			saveOrUpdateBarchart();
		} else if ("Line Chart".equalsIgnoreCase(selectedChartType)) {
			saveOrUpdateLineChart();
		} else if ("Area Chart".equalsIgnoreCase(selectedChartType)) {
			saveOrUpdateAreaChart();
		} else if ("Column Chart".equalsIgnoreCase(selectedChartType)) {
			saveOrUpdateColumnChart();
		} else if ("Pie Chart".equalsIgnoreCase(selectedChartType)) {
			saveOrUpdatePieChart();
		}
		RequestContext.getCurrentInstance().closeDialog(null);
	}

	public void onDialogReturn(SelectEvent event) {
		resetTemplateData();
		loadReportTemplateList();
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public List<String> getChartTypes() {
		return chartTypes;
	}

	public void setChartTypes(List<String> chartTypes) {
		this.chartTypes = chartTypes;
	}

	public String getSelectedChartType() {
		return selectedChartType;
	}

	public void setSelectedChartType(String selectedChartType) {
		this.selectedChartType = selectedChartType;
	}

	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public String getChartTypeImgSrc() {
		return chartTypeImgSrc;
	}

	public void setChartTypeImgSrc(String chartTypeImgSrc) {
		this.chartTypeImgSrc = chartTypeImgSrc;
	}

	public String getSelectedModelDescription() {
		return selectedModelDescription;
	}

	public void setSelectedModelDescription(String selectedModelDescription) {
		this.selectedModelDescription = selectedModelDescription;
	}

	public List<String> getExistingModelNames() {
		return existingModelNames;
	}

	public void setExistingModelNames(List<String> existingModelNames) {
		this.existingModelNames = existingModelNames;
	}

	public List<AttributeMapping> getModelAttrs() {
		return modelAttrs;
	}

	public void setModelAttrs(List<AttributeMapping> modelAttrs) {
		this.modelAttrs = modelAttrs;
	}

	/*
	 * public int getAttributesSize() { if (modelAttrs != null) { return
	 * modelAttrs.size(); } else { return 0; } }
	 */

	public boolean getShowModelDetails() {
		return showModelDetails;
	}

	public void setShowModelDetails(boolean showModelDetails) {
		this.showModelDetails = showModelDetails;
	}

	public String getxAxisTitle() {
		return xAxisTitle;
	}

	public void setxAxisTitle(String xAxisTitle) {
		this.xAxisTitle = xAxisTitle;
	}

	public String getyAxisTitle() {
		return yAxisTitle;
	}

	public void setyAxisTitle(String yAxisTitle) {
		this.yAxisTitle = yAxisTitle;
	}

	public boolean isShowLegend() {
		return showLegend;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	public String getSelectedModelName() {
		return selectedModelName;
	}

	public void setSelectedModelName(String selectedModelName) {
		this.selectedModelName = selectedModelName;
	}

	public Model getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(Model selectedModel) {
		this.selectedModel = selectedModel;
	}

	public boolean isShowDataLabel() {
		return showDataLabel;
	}

	public void setShowDataLabel(boolean showDataLabel) {
		this.showDataLabel = showDataLabel;
	}

	public String getModelDataLabelField() {
		return modelDataLabelField;
	}

	public void setModelDataLabelField(String modelDataLabelField) {
		this.modelDataLabelField = modelDataLabelField;
	}

	public String getModelDataValueField() {
		return modelDataValueField;
	}

	public void setModelDataValueField(String modelDataValueField) {
		this.modelDataValueField = modelDataValueField;
	}

	public String getModelSeriesGroupField() {
		return modelSeriesGroupField;
	}

	public void setModelSeriesGroupField(String modelSeriesGroupField) {
		this.modelSeriesGroupField = modelSeriesGroupField;
	}

	public String getMandatoryTemplateSeriesVlaue() {
		return mandatoryTemplateSeriesVlaue;
	}

	public void setMandatoryTemplateSeriesVlaue(String mandatoryTemplateSeriesVlaue) {
		this.mandatoryTemplateSeriesVlaue = mandatoryTemplateSeriesVlaue;
	}

	public List<TemplateSeries> getTemplateSeriesValues() {
		return templateSeriesValues;
	}

	public void setTemplateSeriesValues(List<TemplateSeries> templateSeriesValues) {
		this.templateSeriesValues = templateSeriesValues;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public boolean isPieChart() {
		return pieChart;
	}

	public void setPieChart(boolean pieChart) {
		this.pieChart = pieChart;
	}

	public String getPieDataField() {
		return pieDataField;
	}

	public void setPieDataField(String pieDataField) {
		this.pieDataField = pieDataField;
	}

	public String getPieCatagoryField() {
		return pieCatagoryField;
	}

	public void setPieCatagoryField(String pieCatagoryField) {
		this.pieCatagoryField = pieCatagoryField;
	}

	public List<BaseReportTemplate> getOrgExtReportTemplateList() {
		return orgExtReportTemplateList;
	}

	public void setOrgExtReportTemplateList(List<BaseReportTemplate> orgExtReportTemplateList) {
		this.orgExtReportTemplateList = orgExtReportTemplateList;
	}

	public List<BaseReportTemplate> getExtReportTemplateList() {
		return extReportTemplateList;
	}

	public void setExtReportTemplateList(List<BaseReportTemplate> extReportTemplateList) {
		this.extReportTemplateList = extReportTemplateList;
	}

	public String getReportDisplayName() {
		return reportDisplayName;
	}

	public void setReportDisplayName(String reportDisplayName) {
		this.reportDisplayName = reportDisplayName;
	}

	public String getSaveOrUpdate() {
		return saveOrUpdate;
	}

	public void setSaveOrUpdate(String saveOrUpdate) {
		this.saveOrUpdate = saveOrUpdate;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public ChartModel getChartModel() {
		return chartModel;
	}

	public void setChartModel(ChartModel chartModel) {
		this.chartModel = chartModel;
	}

	public List<AttributeMapping> getOnlyIntegerModelAttrs() {
		return onlyIntegerModelAttrs;
	}

	public void setOnlyIntegerModelAttrs(List<AttributeMapping> onlyIntegerModelAttrs) {
		this.onlyIntegerModelAttrs = onlyIntegerModelAttrs;
	}

	public int getTabActiveIndex() {
		return tabActiveIndex;
	}

	public void setTabActiveIndex(int tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

}
