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
	
	public static PieChartDataTypeEnum fromName(String name){
		PieChartDataTypeEnum enumValue = null;
		
		if(name!=null){
			for(PieChartDataTypeEnum ref: PieChartDataTypeEnum.values()){
				if(name.equals(ref.name())){
					enumValue = ref;
					break;
				}
			}
		}
				
		return enumValue;
	}
 }
