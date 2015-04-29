package org.reportbay.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.reportbay.common.domain.BaseJPAEntity;

@Entity
@Table(name="model_query")
public class ModelQuery extends BaseJPAEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="model_id")
	private int id;
	private String value;
	@Column(name="join_query")
	private String joinQuery;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getJoinQuery() {
		return joinQuery;
	}

	public void setJoinQuery(String joinQuery) {
		this.joinQuery = joinQuery;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		
		HashCodeBuilder hcb = new HashCodeBuilder(INITIAL_HASH, PRIME_HASH_MULTIPLIER);
		
		hcb.append(id)
		   .append(value);
		
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
		ModelQuery testRef = (ModelQuery) ref;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		eb.append(id, testRef.id)
		  .append(value, testRef.value);
		
		return eb.isEquals();
	}
}
