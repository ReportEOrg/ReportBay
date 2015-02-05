package org.reporte.web.bean.dashboard;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.reporte.report.domain.AreaChartReport;
import org.reporte.report.domain.BarChartReport;
import org.reporte.report.domain.CartesianChartReport;
import org.reporte.report.domain.ColumnChartReport;
import org.reporte.report.domain.LineChartReport;
import org.reporte.report.service.ReportGenerationService;
import org.reporte.report.service.exception.ReportGenerationServiceException;
import org.reporte.reporttemplate.domain.BarChartTemplate;
import org.reporte.reporttemplate.domain.ReportTemplateTypeEnum;
import org.reporte.reporttemplate.domain.TemplateSeries;
import org.reporte.reporttemplate.service.ReportTemplateService;
import org.reporte.reporttemplate.service.exception.ReportTemplateServiceException;
import org.reporte.web.util.ReportUtil;

/**
 * Report Listing JSF Backing bean
 *
 */
@Named("cartesianChartReportBean")
@RequestScoped
public class CartesianChartReportBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CartesianChartModel model;

	private String chartType;

	@Inject
	private ReportGenerationService reportGenService;

	@Inject
	private ReportTemplateService reportTemplateService;

	public void init() {
		// Retrieve the params passed via Dialog Framework.
		Map<String, String> requestParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		boolean preview = requestParams.get("preview") != null ? true : false;
		String templateType = requestParams.get("templateType");
		if (preview) {
			try {
				if ("Area".equals(templateType)) {
					generateAreaChartModel(0);
				} else if ("Line".equals(templateType)) {
					generateLineChartModel(0);
				} else if ("Bar".equals(templateType)) {
					generateBarChartModel(requestParams);
				} else if ("Column".equals(templateType)) {
					generateColumnChartModel(0);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ReportGenerationServiceException e) {
				e.printStackTrace();
			}
		} else {
			try {
				String templateIdStr = requestParams.get("templateId");
				int templateId = Integer.valueOf(templateIdStr);
				if ("Area".equals(templateType)) {
					generateAreaChartModel(templateId);
				} else if ("Line".equals(templateType)) {
					generateLineChartModel(templateId);
				} else if ("Bar".equals(templateType)) {
					generateBarChartModel(templateId);
				} else if ("Column".equals(templateType)) {
					generateColumnChartModel(templateId);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ReportGenerationServiceException e) {
				e.printStackTrace();
			}
		}
	}

	private void generateAreaChartModel(int templateId) throws ReportGenerationServiceException {

		AreaChartReport report = reportGenService.generateAreaChartReport(templateId);

		model = new LineChartModel();

		// chart type
		chartType = "line";

		// map common attributes
		updateChartCommon(model, report);
	}

	private void generateBarChartModel(Map<String, String> chartDetails) throws ReportGenerationServiceException {
		BarChartTemplate chartTemplate = new BarChartTemplate();
		chartTemplate.setTemplateName(chartDetails.get("templateName"));
		chartTemplate.setModelId(Integer.parseInt(chartDetails.get("modelId")));
		chartTemplate.setReportTemplateType(ReportTemplateTypeEnum.SIMPLE);
		chartTemplate.setTitle(chartDetails.get("chartTitle"));
		chartTemplate.setShowLegend(Boolean.valueOf(chartDetails.get("showLegend")));
		boolean xAxisInd = Boolean.valueOf(chartDetails.get("xAxisInd"));
		chartTemplate.setShowXAxis(xAxisInd);
		if (xAxisInd) {
			chartTemplate.setXAxisTitle(chartDetails.get("xAxisTitle"));
		}
		boolean yAxisInd = Boolean.valueOf(chartDetails.get("yAxisInd"));
		chartTemplate.setShowYAxis(yAxisInd);
		if (yAxisInd) {
			chartTemplate.setYAxisTitle(chartDetails.get("yAxisTitle"));
		}
		chartTemplate.setShowDataLabel(Boolean.valueOf(chartDetails.get("showDataLabel")));
		chartTemplate.setModelDataLabelField(chartDetails.get("modelDataLabelField"));
		chartTemplate.setModelDataValueField(chartDetails.get("modelDataValueField"));
		chartTemplate.setModelSeriesGroupField(chartDetails.get("modelSeriesGroupField"));

		String templateSeriesStr = chartDetails.get("templateSeries");
		String[] allTemplateSeries = templateSeriesStr.split(";");
		Set<TemplateSeries> templateSeriesValues = new LinkedHashSet<TemplateSeries>();
		TemplateSeries tempSeris = null;
		if (allTemplateSeries.length > 0) {
			for (int itr = 0; itr < allTemplateSeries.length; itr++) {
				String[] eachTemplateSeries = allTemplateSeries[itr].split("=");
				if (eachTemplateSeries != null && eachTemplateSeries.length > 1) {
					tempSeris = new TemplateSeries();
					tempSeris.setId(itr + 1);
					tempSeris.setName(eachTemplateSeries[0]);
					tempSeris.setModelSeriesValue(eachTemplateSeries[1]);
					templateSeriesValues.add(tempSeris);
				}
			}
		}
		chartTemplate.setDataSeries(templateSeriesValues);
		chartTemplate.setId(123);

		try {
			chartTemplate.setReportQuery(reportTemplateService.constructReportQuery(chartTemplate));
		} catch (ReportTemplateServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BarChartReport report = reportGenService.generateBarChartReport(chartTemplate);

		model = ReportUtil.mapReportToPFModel(report);

		// chart type
		chartType = "bar";

	}

	private void generateBarChartModel(int templateId) throws ReportGenerationServiceException {
		BarChartReport report = reportGenService.generateBarChartReport(templateId);

		model = new BarChartModel();

		// chart type
		chartType = "bar";

		// map common attributes
		updateChartCommon(model, report);
	}

	private void generateColumnChartModel(int templateId) throws ReportGenerationServiceException {
		ColumnChartReport report = reportGenService.generateColumnChartReport(templateId);

		model = new HorizontalBarChartModel();

		// chart type
		chartType = "bar";

		// map common attributes
		updateChartCommon(model, report);
	}

	private void generateLineChartModel(int templateId) throws ReportGenerationServiceException {
		LineChartReport report = reportGenService.generateLineChartReport(templateId);

		model = new LineChartModel();

		// chart type
		chartType = "line";

		// map common attributes
		updateChartCommon(model, report);
	}

	private void updateChartCommon(CartesianChartModel model, CartesianChartReport report) {
		// chart title
		model.setTitle(report.getTitle());

		// X-Axis title
		String xAxisTitle = "";

		if (report.isShowXAxis()) {
			xAxisTitle = report.getXAxisTitle();
		}
		Axis xAxis = new CategoryAxis(xAxisTitle);
		xAxis.setTickAngle(-50);
		model.getAxes().put(AxisType.X, xAxis);

		// Y-Axis title
		Axis yAxis = model.getAxis(AxisType.Y);

		if (report.isShowYAxis()) {
			yAxis.setLabel(report.getYAxisTitle());
		} else {
			yAxis.setLabel("");
		}

		// show datalabel
		model.setShowPointLabels(report.isShowDataLabel());

		// show Legend
		model.setLegendPosition("e");

		ChartSeries modelSeries;

		for (org.reporte.report.domain.ChartSeries reportSeries : report.getChartDataSeries()) {
			modelSeries = new ChartSeries();
			modelSeries.setLabel(reportSeries.getSeriesName());
			model.getSeries().add(modelSeries);

			for (Map.Entry<String, Number> data : reportSeries.getSeriesData().entrySet()) {
				modelSeries.set(data.getKey(), data.getValue());
				// TODO: determine min and max
			}
		}
	}

	/**
	 * @return the model
	 */
	public CartesianChartModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(CartesianChartModel model) {
		this.model = model;
	}

	/**
	 * @return the chartType
	 */
	public String getChartType() {
		return chartType;
	}

	/**
	 * @param chartType
	 *            the chartType to set
	 */
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

}