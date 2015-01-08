package org.reporte.model.domain;

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
		if (this == obj){
			return true;
		}
		if (!super.equals(obj)){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		SimpleModel other = (SimpleModel) obj;
		
		/**
		 * return false (not equals)
		 * if 
		 *    this == null && reference ! =null
		 *    or
		 *    this is not null (safe to access attribute's equal method) && this and reference not equal
		 *    
		 * Note: this == null and reference == null is captured at first test (this == obj)
		 */
		if (table == null ? other.table != null : !table.equals(other.table)){
			return false;
		}
		
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
