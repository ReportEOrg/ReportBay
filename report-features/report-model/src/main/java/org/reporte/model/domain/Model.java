package org.reporte.model.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.reporte.common.interceptor.JPAEntity;

@Entity
@NamedQueries({ @NamedQuery(name = "Model.findAll", query = "SELECT m FROM Model m") })
@Table(name = "model")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Model extends JPAEntity implements Serializable {
	private static final long serialVersionUID = -754304945284440308L;

	public enum Approach {
		SINGLE_TABLE, JOIN_QUERY;
	}

	@TableGenerator(name = "Model_Gen", table = "id_gen", pkColumnName = "gen_name", 
					valueColumnName = "gen_val", pkColumnValue = "Model_Gen", 
					allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="Model_Gen")
	private int id;
	private String name;
	private String description;
	@ManyToOne(optional = false)
	@JoinColumn(name = "datasource_id", nullable = false)
	private Datasource datasource;
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	// nullable = false is very important here. It will force the provider to 
	// set 'foreign key' during 'insert' instead of 'update'. Note: Only 
	// 'Hibernate' provider will honor it. With 'eclipselink', we have to 
	// change the schema to make the foreign key 'nullable' to make it work.
	@JoinColumn(name = "model_id", referencedColumnName = "id", nullable=false)
	@OrderBy("order ASC")
	private List<AttributeMapping> attributeBindings;
	@OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn(name="id", referencedColumnName="model_id") 
	private ModelQuery query;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public abstract Approach getApproach();

	public Datasource getDatasource() {
		return datasource;
	}

	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((attributeBindings == null) ? 0 : attributeBindings
						.hashCode());
		result = prime * result
				+ ((datasource == null) ? 0 : datasource.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Model other = (Model) obj;
		if (attributeBindings == null) {
			if (other.attributeBindings != null)
				return false;
		} else if (!attributeBindings.equals(other.attributeBindings))
			return false;
		if (datasource == null) {
			if (other.datasource != null)
				return false;
		} else if (!datasource.equals(other.datasource))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		return true;
	}
}
