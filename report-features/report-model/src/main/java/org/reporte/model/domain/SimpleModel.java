package org.reporte.model.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@DiscriminatorValue("S")
public class SimpleModel extends Model {
	private static final long serialVersionUID = 8173698435677787885L;
	
	@Column(name="table_name")
	private String table;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}
	
	@Override
	public Approach getApproach() {
		return Approach.SINGLE_TABLE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		
		HashCodeBuilder hcb = new HashCodeBuilder(INITIAL_HASH, PRIME_HASH_MULTIPLIER);
		
		hcb.appendSuper(super.hashCode())
		   .append(table);
		
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
		
		SimpleModel testRef = (SimpleModel) ref;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		eb.append(table, testRef.table);
		
		return eb.isEquals();
	}

	@Override
	public String toString() {
		return "SimpleModel [getTable()=" + getTable() + ", getApproach()="
				+ getApproach() + ", getId()=" + getId() + ", getName()="
				+ getName() + ", getDescription()=" + getDescription()
				+ ", getDatasource()=" + getDatasource()
				+ ", getAttributeBindings()=" + getAttributeBindings()
				+ ", getQuery()=" + getQuery() + "]";
	}
}
