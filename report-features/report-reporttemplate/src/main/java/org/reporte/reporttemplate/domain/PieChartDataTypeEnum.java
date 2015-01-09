package org.reporte.reporttemplate.domain;

public enum PieChartDataTypeEnum { 
	VALUE("V"),
	PERCENT("P");

	private String code;
	
	private PieChartDataTypeEnum(String shortCode){
		code = shortCode;
	}
	
	public String getCode(){
		return code;
	}
 }
