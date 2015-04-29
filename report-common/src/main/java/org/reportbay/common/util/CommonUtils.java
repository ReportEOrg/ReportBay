package org.reportbay.common.util;

public class CommonUtils {
	
	/**
	 * 
	 * @param obj
	 * @param refName
	 */
	public static void checkForNull(Object obj, String refName) {
		if (obj == null) {
			throw new IllegalArgumentException("'" + refName + "' must not be Null.");
		}
	}
}
