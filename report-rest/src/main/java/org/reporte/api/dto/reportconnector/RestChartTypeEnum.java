package org.reporte.api.dto.reportconnector;


public enum RestChartTypeEnum{
	AREA,
	BAR,
	COLUMN,
	LINE,
	PIE;
	
	public static RestChartTypeEnum fromName(String name){
		RestChartTypeEnum enumValue = null;
				
		if(name!=null){
			for(RestChartTypeEnum ref: RestChartTypeEnum.values()){
				if(name.equals(ref.name())){
					enumValue = ref;
					break;
				}
			}
		}
				
		return enumValue;
	}
}