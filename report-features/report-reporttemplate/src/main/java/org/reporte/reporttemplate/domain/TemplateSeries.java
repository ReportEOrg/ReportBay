package org.reporte.reporttemplate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.reporte.common.domain.BaseJPAEntity;

@Entity
@Table(name="template_series")
public class TemplateSeries extends BaseJPAEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@TableGenerator(name = "ReportTemplateSeries_Gen", 
		    table = "id_gen", 
		    pkColumnName = "gen_name", pkColumnValue = "ReportTemplateSeries_Gen", 
		    valueColumnName = "gen_val", allocationSize = 1)

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="ReportTemplateSeries_Gen")
	private int id;

	/**
	 * 
	 */
	@Column(name="name")
	private String name;
	/**
	 * 
	 */
	@Column(name="model_field")
	private String modelField;
	/**
	 * 
	 * @return 
	 */
	public String getName() {
	 	 return name; 
	}
	/**
	 * 
	 * @param name 
	 */
	public void setName(String name) { 
		 this.name = name; 
	}
	/**
	 * 
	 * @return 
	 */
	public String getModelField() {
	 	 return modelField; 
	}
	/**
	 * 
	 * @param modelField 
	 */
	public void setModelField(String modelField) { 
		 this.modelField = modelField; 
	}
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
	 * {@inheritDoc}
	 */
	public boolean equals(Object ref){
		if(!super.equals(ref)){
			return false;
		}
		
		TemplateSeries testRef = (TemplateSeries)ref;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		eb.append(id, testRef.id)
		  .append(modelField, testRef.modelField)
		  .append(name, testRef.name);
		
		return eb.isEquals();
	}
	/**
	 * {@inheritDoc}
	 */
	public int hashCode(){
		
		HashCodeBuilder hcb = new HashCodeBuilder(INITIAL_HASH, PRIME_HASH_MULTIPLIER);
		
		hcb.append(id)
		   .append(modelField)
		   .append(name);
		
		return hcb.toHashCode();
	}
}
