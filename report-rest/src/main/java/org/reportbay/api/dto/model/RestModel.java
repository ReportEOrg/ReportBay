package org.reportbay.api.dto.model;

import java.io.Serializable;
import java.util.List;

import org.reportbay.datasource.domain.Datasource;
import org.reportbay.model.domain.AttributeMapping;
import org.reportbay.model.domain.Model;
import org.reportbay.model.domain.ModelQuery;
import org.reportbay.model.domain.Model.Approach;

/***
 * Wrapper class for JSON pojo
 */
public class RestModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String description;
	private Datasource datasource;
	private List<AttributeMapping> attributeBindings;
	private ModelQuery query;
	private String table;
	private Approach approach;
	
	public RestModel(){
		super();
	}
	/**
	 * helper constructor to map entity domain to REST domain
	 * @param model
	 */
	public RestModel(Model model){
		id = model.getId();
		name = model.getName();
		description = model.getDescription();
		datasource = model.getDatasource();
		attributeBindings = model.getAttributeBindings();
		query = model.getQuery();
		//table attribute only for simple model
		approach = model.getApproach();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Datasource getDatasource() {
		return datasource;
	}

	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AttributeMapping> getAttributeBindings() {
		return attributeBindings;
	}

	public void setAttributeBindings(List<AttributeMapping> attributeBindings) {
		this.attributeBindings = attributeBindings;
	}

	public ModelQuery getQuery() {
		return query;
	}

	public void setQuery(ModelQuery query) {
		this.query = query;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Model.Approach getApproach() {
		return approach;
	}

	public void setApproach(Model.Approach approach) {
		this.approach = approach;
	}
}