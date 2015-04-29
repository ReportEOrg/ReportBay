package org.reportbay.api.dto.publish;

import java.io.Serializable;
import java.util.Properties;

public class Publish implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String option;
	private String publishName;
	
	private Properties params;

	/**
	 * @return the params
	 */
	public Properties getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Properties params) {
		this.params = params;
	}

	/**
	 * @return the option
	 */
	public String getOption() {
		return option;
	}

	/**
	 * @param option the option to set
	 */
	public void setOption(String option) {
		this.option = option;
	}

	/**
	 * @return the publishName
	 */
	public String getPublishName() {
		return publishName;
	}

	/**
	 * @param publishName the publishName to set
	 */
	public void setPublishName(String publishName) {
		this.publishName = publishName;
	}

}