package org.reportbay.api.dto.reportconnector;


public enum TemplateType{
	AREA,
	BAR,
	COLUMN,
	LINE,
	PIE,
	CROSSTAB;
	
	public static TemplateType fromName(String name){
		TemplateType enumValue = null;
				
		if(name!=null){
			for(TemplateType ref: TemplateType.values()){
				if(name.equals(ref.name())){
					enumValue = ref;
					break;
				}
			}
		}
				
		return enumValue;
	}
}