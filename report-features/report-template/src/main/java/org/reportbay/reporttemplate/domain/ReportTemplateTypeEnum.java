package org.reportbay.reporttemplate.domain;

public enum ReportTemplateTypeEnum { 
	SIMPLE("S"),
	CUSTOM("C");

	private String code;
	
	private ReportTemplateTypeEnum(String shortCode){
		code = shortCode;
	}
	
	public String getCode(){
		return code;
	}
 }
