package org.reporte.model.domain;

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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.reporte.common.domain.BaseJPAEntity;
import org.reporte.datasource.domain.Datasource;

@Entity
@NamedQueries({ @NamedQuery(name = "Model.findAll", query = "SELECT m FROM Model m") })
@Table(name = "model")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Model extends BaseJPAEntity{
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		
		HashCodeBuilder hcb = new HashCodeBuilder(INITIAL_HASH, PRIME_HASH_MULTIPLIER);
		
		//use method instead of attribute for those possible lazy load (e.g. joinColumn) 
		hcb.append(getAttributeBindings())
		   .append(getDatasource())
		   .append(description)
		   .append(id)
		   .append(name)
		   .append(getQuery());
		
		return hcb.toHashCode();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object ref) {
		
		if(!super.equals(ref)){
			return false;
		}

		Model testRef = (Model) ref;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		//use method instead of attribute for those possible lazy load (e.g. joinColumn) 
		eb.append(getAttributeBindings(), testRef.getAttributeBindings())
		  .append(getDatasource(), testRef.getDatasource())
		  .append(description, testRef.description)
		  .append(id, testRef.id)
		  .append(name, testRef.name)
		  .append(getQuery(), testRef.getQuery());

		return eb.isEquals();
	}
}
