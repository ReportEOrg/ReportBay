package org.reporte.web.bean.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.reporte.reporttemplate.domain.AreaChartTemplate;
import org.reporte.reporttemplate.domain.BarChartTemplate;
import org.reporte.reporttemplate.domain.BaseReportTemplate;
import org.reporte.reporttemplate.domain.ColumnChartTemplate;
import org.reporte.reporttemplate.domain.LineChartTemplate;
import org.reporte.reporttemplate.domain.PieChartTemplate;
import org.reporte.reporttemplate.service.ReportTemplateService;
import org.reporte.reporttemplate.service.exception.ReportTemplateServiceException;
import org.reporte.web.common.ChartTypeEnum;

/**
 * Report Listing JSF Backing bean 
 *
 */
@Named("reportListBean")
@ViewScoped
public class ReportListBean implements Serializable{

	private static final Logger LOG = Logger.getLogger(ReportListBean.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ReportTemplateService reportTemplateService;
	

	private List<BaseReportTemplate> reportList = new ArrayList<BaseReportTemplate>();
	
	public void init() {
		buildReportList();
	}

	/**
	 * @return the reportList
	 */
	public List<BaseReportTemplate> getReportList() {
		
		return reportList;
	}

	/**
	 * @param reportList the reportList to set
	 */
	public void setReportList(List<BaseReportTemplate> reportList) {
		this.reportList = reportList;
	}
	
	private void buildReportList(){
		try{
			reportList.clear();
		
			reportList.addAll(reportTemplateService.findAllReportTemplate());
		}
		catch(ReportTemplateServiceException rtse){
			LOG.error("failed to find all report template");
		}
	}
	
	/**
	 * Ajax action event handler method
	 * @param actionEvent
	 */
	public void displayReportDetails(ActionEvent actionEvent){
		Object obj = actionEvent.getComponent().getAttributes().get("viewReport");
				
		Map<String, List<String>> requestParams = new HashMap<String, List<String>>();
		
		requestParams.put("templateId", new ArrayList<String>());
		requestParams.put("templateType", new ArrayList<String>());
		
		requestParams.get("templateId").add(String.valueOf(((BaseReportTemplate)obj).getId()));
		requestParams.get("templateType").add(detemineChartType((BaseReportTemplate)obj));
		
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("draggable", false);
		options.put("resizable", true);
		options.put("contentWidth", 800);
		options.put("contentHeight", 400);
		
		RequestContext.getCurrentInstance().openDialog("display_chart_report", options, requestParams);
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	private String detemineChartType(BaseReportTemplate obj){
		
		String chartType = null;

		if(obj instanceof PieChartTemplate){
			chartType = ChartTypeEnum.PIE.name();
		}
		else if(obj instanceof LineChartTemplate){
			chartType = ChartTypeEnum.LINE.name();
		}
		else if(obj instanceof AreaChartTemplate){
			chartType = ChartTypeEnum.AREA.name();
		}
		else if(obj instanceof BarChartTemplate){
			chartType = ChartTypeEnum.BAR.name();
		}
		else if(obj instanceof ColumnChartTemplate){
			chartType = ChartTypeEnum.COLUMN.name();
		}
		
		return chartType;
	}
}