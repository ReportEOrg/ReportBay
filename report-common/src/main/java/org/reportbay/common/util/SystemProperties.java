package org.reportbay.common.util;

import java.util.Properties;

public class SystemProperties {
	
	private static Properties properties = System.getProperties();
	
	public static String getProperty(String key){
		return properties.getProperty(key);
	}

	public static Properties getProperties(){
		return properties;
	}
}
