package org.reporte.web.component;

import java.io.Serializable;

public class ModelNode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String modelName;
	
	public ModelNode(String modelName){
		this.modelName = modelName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
}