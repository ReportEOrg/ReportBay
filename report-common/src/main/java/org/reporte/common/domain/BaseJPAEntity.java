package org.reporte.common.domain;

import java.io.Serializable;

import org.reporte.common.interceptor.JPAEntity;

/**
 * 
 * act as parent class to all JPA entity with hashCode implementation util logic 
 *
 */
public class BaseJPAEntity extends JPAEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static final int PRIME_HASH_MULTIPLIER = 31;
	protected static final int INITIAL_HASH = 1;
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object ref){
		//same object reference, equal
		if(this==ref){
			return true;
		}

		//TODO: check will this fail when class is via proxy?
		//reference is null or of different class, not equals
		if(ref==null || (ref.getClass() != this.getClass())){
			return false;
		}
		
		return true;
	}
}