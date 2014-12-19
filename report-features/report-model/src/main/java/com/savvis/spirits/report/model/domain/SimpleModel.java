package com.savvis.spirits.report.model.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleModel other = (SimpleModel) obj;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
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
