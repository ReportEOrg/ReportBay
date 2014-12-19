package com.savvis.spirits.report.common.util;

public class CommonUtils {
	
	public static void checkForNull(Object obj, String refName) {
		if (obj == null) {
			throw new IllegalArgumentException("'" + refName + "' must not be Null.");
		}
	}
}
