package org.reportbay.datasource.domain;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ColumnMetadata implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String label;
	private String typeName;
	private String className;
	private int order;
	
	public ColumnMetadata() {
		// Default Constructor.
	}
	
	public ColumnMetadata(String label, String typeName, String className, int order) {
		this.label = label;
		this.typeName = typeName;
		this.className = className;
		this.order = order;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		
		HashCodeBuilder hcb = new HashCodeBuilder(1,31);
		
		hcb.append(className)
		   .append(label)
		   .append(order)
		   .append(typeName);
		
		return hcb.toHashCode(); 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}

		if (obj == null || (getClass() != obj.getClass())){
			return false;
		}
		ColumnMetadata other = (ColumnMetadata) obj;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		eb.append(className, other.className)
		  .append(label, other.label)
		  .append(order, other.order)
		  .append(typeName, other.typeName);
		
		return eb.isEquals();
	}
	
	@Override
	public String toString() {
		return "ColumnMetadata [label=" + label + ", typeName=" + typeName
				+ ", className=" + className + ", order=" + order + "]";
	}
}
