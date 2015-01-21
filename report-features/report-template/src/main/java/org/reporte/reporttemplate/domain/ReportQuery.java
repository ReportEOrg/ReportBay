package org.reporte.reporttemplate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.reporte.common.domain.BaseJPAEntity;
import org.reporte.datasource.domain.Datasource;

//entity bean
@Entity
//table
@Table(name = "report_query")
public class ReportQuery extends BaseJPAEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * use same id as the report template (loosely coupled)
	 */
	@Id
	private int id;
	
	@Column(name="query")
	private String query;
	
	@ManyToOne
	@JoinColumn(name="datasource_id", nullable=false)
	private Datasource datasource;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * @return the datasource
	 */
	public Datasource getDatasource() {
		return datasource;
	}

	/**
	 * @param datasource the datasource to set
	 */
	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}
}