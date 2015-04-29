package org.reportbay.api.dto.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * Wrapper class for JSON pojo
 */
public class RestModels implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<RestModel> models = new ArrayList<RestModel>();
	
	/**
	 * 
	 * @return
	 */
	public List<RestModel> getModels() {
		return models;
	}
	/**
	 * 
	 * @param models
	 */
	public void setModels(List<RestModel> models) {
		this.models = models;
	}
}