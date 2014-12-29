package org.reporte.model.domain;

public class ColumnMetadata {

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + order;
		result = prime * result
				+ ((typeName == null) ? 0 : typeName.hashCode());
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
		ColumnMetadata other = (ColumnMetadata) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (order != other.order)
			return false;
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "ColumnMetadata [label=" + label + ", typeName=" + typeName
				+ ", className=" + className + ", order=" + order + "]";
	}
}
