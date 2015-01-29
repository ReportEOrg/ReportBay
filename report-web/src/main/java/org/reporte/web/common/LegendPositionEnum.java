package org.reporte.web.common;

/**
 * 
 * Prime Faces chart Legend Position enum
 *
 */
public enum LegendPositionEnum{
	
	NOTRH("n"),
	NORTH_EAST("ne"),
	EAST("e"),
	SOUTH_EAST("se"),
	SOUTH("s"),
	SOUTH_WEST("sw"),
	WEST("w"),
	NORTH_WEST("nw");
	
	private String code;
	
	private LegendPositionEnum(String value) {
		code = value;
	}
	
	public String getCode(){
		return code;
	}
}