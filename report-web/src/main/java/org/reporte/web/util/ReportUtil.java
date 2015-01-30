package org.reporte.web.util;

import java.util.Map;

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
import org.reporte.web.common.LegendPositionEnum;

/**
 * 
 * Mapper utility class that converting report POJO to Primefaces POJO
 *
 */
public class ReportUtil{
	

	/**
	 * mapping area chart report to primeFace LineChartModel
	 * @param report
	 * @return
	 */
	public static LineChartModel mapReportToPFModel(AreaChartReport report){

		return mapReportToPFModel(report, -60);
	}
	
	/**
	 * mapping area chart report to primeFace LineChartModel
	 * @param report
	 * @param xAxisLabelTickAngle
	 * @return
	 */
	public static LineChartModel mapReportToPFModel(AreaChartReport report, int xAxisLabelTickAngle){
		LineChartModel model = new LineChartModel();
		
		//map common attributes
		updateLineChartCommon(model, report,-60);
		
		//update Area series
		updateAreaSeries(model, report);
		
		return model;
	}
	
	/**
	 * mapping bar chart report to primeFace BarChartModel
	 * @param report
	 * @return
	 */
	public static BarChartModel mapReportToPFModel(BarChartReport report){
		BarChartModel model = new BarChartModel();
		
		//map common attributes
		updateBarChartCommon(model, report, -60);
		
		//map chart series
		updateChartSeries(model,report);
		
		return model;
	}
	
	/**
	 * mapping column chart report to primeFace HorizontalBarChartModel
	 * @param report
	 * @return
	 */
	public static HorizontalBarChartModel mapReportToPFModel(ColumnChartReport report){
		HorizontalBarChartModel model = new HorizontalBarChartModel();
		
		//map common attributes
		updateBarChartCommon(model, report);
				
		//map chart series
		updateChartSeries(model,report);
		
		return model;
	}
	/**
	 * mapping Line chart report to primeFace LineChartModel
	 * @param report
	 * @return
	 */
	public static LineChartModel mapReportToPFModel(LineChartReport report){
		LineChartModel model = new LineChartModel();
		
		//map common attributes
		updateLineChartCommon(model, report, -60);
		
		//map line series 
		updateLineSeries(model, report);
		
		return model;
	}
	/**
	 * mapping pie chart report to primeFace PieChartModel
	 * @param report
	 * @return
	 */
	public static PieChartModel mapReportToPFModel(PieChartReport report){
		
		PieChartModel model = new PieChartModel();
		
		updatePieChart(model, report);
		
		return model;
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
	private static void updatePieChart(PieChartModel model, PieChartReport report){
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
	 * 
	 * @param model
	 * @param report
	 */
	private static void updateBarChartCommon(CartesianChartModel model, CartesianChartReport report){
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
	private static void updateBarChartCommon(CartesianChartModel model, CartesianChartReport report, int tickAngle){
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
	 * update line chart common
	 * 1) common chart attribute
	 * 2) line chart specific X-axis
	 * @param model
	 * @param report
	 * @param tickAngle
	 */
	private static void updateLineChartCommon(CartesianChartModel model, CartesianChartReport report, int tickAngle){
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
	 * update cartesian chart (exclude x-axis)
	 * 1) common chart attribute
	 * 2) y-axis
	 * 3) data label
	 * @param model
	 * @param report
	 */
	private static void updateCartesianChartCommon(CartesianChartModel model, CartesianChartReport report){
		
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
	 * chart common attribute
	 * 1) title
	 * 2) show legend
	 * 
	 * @param model
	 * @param report
	 */
	private static void updateChartCommon(ChartModel model, ChartReport report){
		//chart title
		model.setTitle(report.getTitle());
		
		//show legend
		if(report.isShowLegend()){
			model.setLegendPosition(LegendPositionEnum.EAST.getCode());
		}
	}
	/**
	 * 
	 * @param model
	 * @param report
	 */
	private static void updateLineSeries(CartesianChartModel model, CartesianChartReport report){
		updateCommonLineSeries(model, report, false);
	}
	
	/**
	 * 
	 * @param model
	 * @param report
	 */
	private static void updateAreaSeries(CartesianChartModel model, CartesianChartReport report){
		updateCommonLineSeries(model, report, true);
	}

	/**
	 * update line/area chart series
	 * @param model
	 * @param report
	 * @param fill
	 */
	private static void updateCommonLineSeries(CartesianChartModel model, CartesianChartReport report, boolean fill){
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
	 * update bar/column chart series
	 * 
	 * @param model
	 * @param report
	 */
	private static void updateChartSeries(CartesianChartModel model, CartesianChartReport report){
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
}