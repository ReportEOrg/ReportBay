package org.reporte.web.common;

/**
 * 
 * Chart type enum with mapping to primefaces <p:chart type value 
 *
 */
public enum ChartTypeEnum{
	
	AREA("line"),
	BAR("bar"),
	COLUMN("bar"),
	LINE("line"),
	PIE("pie");
	
	private String code;
	
	private ChartTypeEnum(String code){
		this.code = code;
	}
	
	public String getCode(){
		return code;
	}
	
	public static ChartTypeEnum fromName(String name){
		ChartTypeEnum enumValue = null;
				
		if(name!=null){
			for(ChartTypeEnum ref: ChartTypeEnum.values()){
				if(name.equals(ref.name())){
					enumValue = ref;
					break;
				}
			}
		}
				
		return enumValue;
	}
}