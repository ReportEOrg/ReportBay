package org.reporte.web.bean.report;

import static org.reporte.web.common.ChartTypeEnum.AREA;
import static org.reporte.web.common.ChartTypeEnum.BAR;
import static org.reporte.web.common.ChartTypeEnum.COLUMN;
import static org.reporte.web.common.ChartTypeEnum.LINE;
import static org.reporte.web.common.ChartTypeEnum.PIE;

import java.io.Serializable;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartModel;
import org.reporte.report.domain.AreaChartReport;
import org.reporte.report.domain.BarChartReport;
import org.reporte.report.domain.ColumnChartReport;
import org.reporte.report.domain.LineChartReport;
import org.reporte.report.domain.PieChartReport;
import org.reporte.report.service.ReportGenerationService;
import org.reporte.report.service.exception.ReportGenerationServiceException;
import org.reporte.web.util.ReportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Chart Report JSF Backing bean 
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
		
		//chart type
		chartType= PIE.getCode();
		
		model = ReportUtil.mapReportToPFModel(report);
	}
	
	/**
	 * 
	 * @param templateId
	 * @throws ReportGenerationServiceException
	 */
	private void generateAreaChartModel(int templateId) throws ReportGenerationServiceException{
		
		AreaChartReport report = reportGenService.generateAreaChartReport(templateId);
		
		//chart type
		chartType = AREA.getCode();
		
		model = ReportUtil.mapReportToPFModel(report);
	}
	
	/**
	 * 
	 * @param templateId
	 * @throws ReportGenerationServiceException
	 */
	private void generateBarChartModel(int templateId) throws ReportGenerationServiceException{
		BarChartReport report = reportGenService.generateBarChartReport(templateId);
		
		//chart type
		chartType = BAR.getCode();
		
		model = ReportUtil.mapReportToPFModel(report);
	}
	/**
	 * 
	 * @param templateId
	 * @throws ReportGenerationServiceException
	 */
	private void generateColumnChartModel(int templateId) throws ReportGenerationServiceException{
		ColumnChartReport report = reportGenService.generateColumnChartReport(templateId);
		
		//chart type
		chartType = COLUMN.getCode();
		
		model = ReportUtil.mapReportToPFModel(report);
	}
	/**
	 * 
	 * @param templateId
	 * @throws ReportGenerationServiceException
	 */
	private void generateLineChartModel(int templateId) throws ReportGenerationServiceException{
		LineChartReport report = reportGenService.generateLineChartReport(templateId);
		
		//chart type
		chartType = LINE.getCode();
		
		model = ReportUtil.mapReportToPFModel(report);
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