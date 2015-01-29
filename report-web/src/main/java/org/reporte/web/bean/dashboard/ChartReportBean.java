package org.reporte.web.bean.dashboard;

import java.io.Serializable;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.PieChartModel;
import org.reporte.report.domain.AreaChartReport;
import org.reporte.report.domain.BarChartReport;
import org.reporte.report.domain.CartesianChartReport;
import org.reporte.report.domain.ChartReport;
import org.reporte.report.domain.ColumnChartReport;
import org.reporte.report.domain.LineChartReport;
import org.reporte.report.domain.PieChartReport;
import org.reporte.report.service.ReportGenerationService;
import org.reporte.report.service.exception.ReportGenerationServiceException;
import static org.reporte.web.common.ChartTypeEnum.AREA;
import static org.reporte.web.common.ChartTypeEnum.BAR;
import static org.reporte.web.common.ChartTypeEnum.COLUMN;
import static org.reporte.web.common.ChartTypeEnum.LINE;
import static org.reporte.web.common.ChartTypeEnum.PIE;
import org.reporte.web.common.LegendPositionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Report Listing JSF Backing bean 
 *
 */
@Named("chartReportBean")
@RequestScoped
public class ChartReportBean implements Serializable{
	
	private static final Logger LOG = LoggerFactory.getLogger(ChartReportBean.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ChartModel model;
	
	private String chartType;
	
	@Inject
	private ReportGenerationService reportGenService;

	public void init() {
		// Retrieve the params passed via Dialog Framework.
		Map<String, String> requestParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String templateIdStr = requestParams.get("templateId");
		String templateType = requestParams.get("templateType");
		
		try {
			int templateId = Integer.valueOf(templateIdStr);

			if(AREA.name().equals(templateType)){
				generateAreaChartModel(templateId);
			}
			else if(LINE.name().equals(templateType)){
				generateLineChartModel(templateId);
			}
			else if(BAR.name().equals(templateType)){
				generateBarChartModel(templateId);
			}
			else if(COLUMN.name().equals(templateType)){
				generateColumnChartModel(templateId);
			}
			else if(PIE.name().equals(templateType)){
				generatePieChartModel(templateId);
			}
		} catch (NumberFormatException e) {
			LOG.error("invalid template id "+templateIdStr, e);
		} catch (ReportGenerationServiceException e) {
			LOG.error("error generating report ",e);
		}
	}
	
	/**
	 * 
	 * @param templateId
	 * @throws ReportGenerationServiceException
	 */
	private void generatePieChartModel(int templateId) throws ReportGenerationServiceException{
		PieChartReport report = reportGenService.generatePieChartReport(templateId);
		
		PieChartModel pcModel = new PieChartModel();
		
		//chart type
		chartType= PIE.getCode();
		
		updatePieChart(pcModel, report);
		
		model = pcModel;
	}
	
	/**
	 * 
	 * @param templateId
	 * @throws ReportGenerationServiceException
	 */
	private void generateAreaChartModel(int templateId) throws ReportGenerationServiceException{
		
		AreaChartReport report = reportGenService.generateAreaChartReport(templateId);
		
		CartesianChartModel ccModel = new LineChartModel();
		
		//chart type
		chartType = AREA.getCode();
		
		//map common attributes
		updateLineChartCommon(ccModel, report,-60);

		//update Area series
		updateAreaSeries(ccModel, report);
		
		model = ccModel;
	}
	
	/**
	 * 
	 * @param templateId
	 * @throws ReportGenerationServiceException
	 */
	private void generateBarChartModel(int templateId) throws ReportGenerationServiceException{
		BarChartReport report = reportGenService.generateBarChartReport(templateId);
		
		CartesianChartModel ccModel = new BarChartModel();
		
		//chart type
		chartType = BAR.getCode();
		
		//map common attributes
		updateBarChartCommon(ccModel, report, -60);
		
		//map chart series
		updateChartSeries(ccModel,report);
		
		model = ccModel;
	}
	/**
	 * 
	 * @param templateId
	 * @throws ReportGenerationServiceException
	 */
	private void generateColumnChartModel(int templateId) throws ReportGenerationServiceException{
		ColumnChartReport report = reportGenService.generateColumnChartReport(templateId);
		
		CartesianChartModel ccModel = new HorizontalBarChartModel();

		//chart type
		chartType = COLUMN.getCode();
		
		//map common attributes
		updateBarChartCommon(ccModel, report);
		
		//map chart series
		updateChartSeries(ccModel,report);
		
		model = ccModel;
	}
	/**
	 * 
	 * @param templateId
	 * @throws ReportGenerationServiceException
	 */
	private void generateLineChartModel(int templateId) throws ReportGenerationServiceException{
		LineChartReport report = reportGenService.generateLineChartReport(templateId);
		
		CartesianChartModel ccModel = new LineChartModel();
		
		//chart type
		chartType = LINE.getCode();
		
		//map common attributes
		updateLineChartCommon(ccModel, report, -60);
		
		//map line series 
		updateLineSeries(ccModel, report);
		
		model = ccModel;
	}
	
	/**
	 * update pie chart
	 * 1) common chart attribute
	 * 2) show data label
	 * 3) pie chart categories
	 * 
	 * @param model
	 * @param report
	 */
	private void updatePieChart(PieChartModel model, PieChartReport report){
		//1. update chart common attribute
		updateChartCommon(model, report);

		//2. show data label
		model.setShowDataLabels(report.isShowDataLabel());
		
		//3. categories
		Map<String, Number> categoryDataMap = report.getCategoryData();
		
		for(Map.Entry<String, Number> entry: categoryDataMap.entrySet()){
			model.set(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * update line chart common
	 * 1) common chart attribute
	 * 2) line chart specific X-axis
	 * @param model
	 * @param report
	 * @param tickAngle
	 */
	private void updateLineChartCommon(CartesianChartModel model, CartesianChartReport report, int tickAngle){
		//1. update cartesian chart common (exclude x-axis)
		updateCartesianChartCommon(model,report);
		
		//2. line chart handling of setting X-Axis, must override instead of use existing from model
		String xAxisTitle = "";
        
		if(report.isShowXAxis()){
			xAxisTitle = report.getXAxisTitle();
		}
		Axis xAxis = new CategoryAxis(xAxisTitle);
		xAxis.setTickAngle(tickAngle);
		
		model.getAxes().put(AxisType.X,xAxis);
	}
	
	/**
	 * 
	 * @param model
	 * @param report
	 */
	private void updateBarChartCommon(CartesianChartModel model, CartesianChartReport report){
		updateBarChartCommon(model, report, 0);
	}
	
	/**
	 * update bar chart common
	 * 1) common chart attribute
	 * 2) bar char specific setting of x-axis
	 * @param model
	 * @param report
	 * @param tickAngle
	 */
	private void updateBarChartCommon(CartesianChartModel model, CartesianChartReport report, int tickAngle){
		//1. update cartesian chart common (exclude x-axis)
		updateCartesianChartCommon(model,report);
		
		//2. bar chart handling of setting X-Axis (not allowed to add, but take from model)
		String xAxisTitle = "";
        
		if(report.isShowXAxis()){
			xAxisTitle = report.getXAxisTitle();
		}
		Axis xAxis = model.getAxis(AxisType.X);
		xAxis.setLabel(xAxisTitle);
		xAxis.setTickAngle(tickAngle);
	}
	
	/**
	 * chart common attribute
	 * 1) title
	 * 2) show legend
	 * 
	 * @param model
	 * @param report
	 */
	private void updateChartCommon(ChartModel model, ChartReport report){
		//chart title
		model.setTitle(report.getTitle());
		
		//show legend
		if(report.isShowLegend()){
			model.setLegendPosition(LegendPositionEnum.EAST.getCode());
		}
	}
	
	/**
	 * update cartesian chart (exclude x-axis)
	 * 1) common chart attribute
	 * 2) y-axis
	 * 3) data label
	 * @param model
	 * @param report
	 */
	private void updateCartesianChartCommon(CartesianChartModel model, CartesianChartReport report){
		
		//update chart common attribute
		updateChartCommon(model, report);
		
		//Y-Axis title
		Axis yAxis = model.getAxis(AxisType.Y);

		if(report.isShowYAxis()){
			yAxis.setLabel(report.getYAxisTitle());
		}
		else{
			yAxis.setLabel("");
		}

		//show datalabel
		model.setShowPointLabels(report.isShowDataLabel());
		
	}
	/**
	 * update bar/column chart series
	 * 
	 * @param model
	 * @param report
	 */
	private void updateChartSeries(CartesianChartModel model, CartesianChartReport report){
		ChartSeries modelSeries;

		for(org.reporte.report.domain.ChartSeries reportSeries: report.getChartDataSeries()){
			modelSeries = new ChartSeries();
			modelSeries.setLabel(reportSeries.getSeriesName());
			model.getSeries().add(modelSeries);

			for(Map.Entry<String, Number> data : reportSeries.getSeriesData().entrySet()){
				modelSeries.set(data.getKey(), data.getValue());
				//TODO: determine min and max
			}
		}
	}

	/**
	 * 
	 * @param model
	 * @param report
	 */
	private void updateLineSeries(CartesianChartModel model, CartesianChartReport report){
		updateCommonLineSeries(model, report, false);
	}
	
	/**
	 * 
	 * @param model
	 * @param report
	 */
	private void updateAreaSeries(CartesianChartModel model, CartesianChartReport report){
		updateCommonLineSeries(model, report, true);
	}

	/**
	 * update line/area chart series
	 * @param model
	 * @param report
	 * @param fill
	 */
	private void updateCommonLineSeries(CartesianChartModel model, CartesianChartReport report, boolean fill){
		LineChartSeries modelSeries;

		for(org.reporte.report.domain.ChartSeries reportSeries: report.getChartDataSeries()){
			modelSeries = new LineChartSeries();
			modelSeries.setLabel(reportSeries.getSeriesName());

			//fill area
			modelSeries.setFill(fill);;

			model.getSeries().add(modelSeries);
			
			for(Map.Entry<String, Number> data : reportSeries.getSeriesData().entrySet()){
				modelSeries.set(data.getKey(), data.getValue());
				//TODO: determine min and max
			}
		}
	}

	/**
	 * @return the model
	 */
	public ChartModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
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
	 * @param chartType the chartType to set
	 */
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	
}