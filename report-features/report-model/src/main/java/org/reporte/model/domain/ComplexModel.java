package org.reporte.model.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("C")
public class ComplexModel extends Model {
	private static final long serialVersionUID = 5666779367510673463L;
	
	@Override
	public Approach getApproach() {
		return Approach.JOIN_QUERY;
	}

	@Override
	public String toString() {
		return "ComplexModel [getApproach()=" + getApproach() + ", getId()="
				+ getId() + ", getName()=" + getName() + ", getDescription()="
				+ getDescription() + ", getDatasource()=" + getDatasource()
				+ ", getAttributeBindings()=" + getAttributeBindings()
				+ ", getQuery()=" + getQuery() + "]";
	}
}
