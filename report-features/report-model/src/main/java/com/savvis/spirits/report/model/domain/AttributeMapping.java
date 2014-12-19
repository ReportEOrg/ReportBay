package com.savvis.spirits.report.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "attribute_mapping")
public class AttributeMapping {
	@TableGenerator(name = "AttrMapping_Gen", table = "ID_GEN", pkColumnName = "GEN_NAME", 
					valueColumnName = "GEN_VAL", pkColumnValue = "AttrMapping_Gen", 
					allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "AttrMapping_Gen")
	private int id;
	@Column(name = "referenced_column")
	private String referencedColumn;
	private String alias;
	@Column(name = "type_name")
	private String typeName;
	@Column(name = "`order`")
	private int order;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReferencedColumn() {
		return referencedColumn;
	}

	public void setReferencedColumn(String referencedColumn) {
		this.referencedColumn = referencedColumn;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
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
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result + id;
		result = prime * result + order;
		result = prime * result + ((referencedColumn == null) ? 0 : referencedColumn.hashCode());
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
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
		AttributeMapping other = (AttributeMapping) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		if (id != other.id)
			return false;
		if (order != other.order)
			return false;
		if (referencedColumn == null) {
			if (other.referencedColumn != null)
				return false;
		} else if (!referencedColumn.equals(other.referencedColumn))
			return false;
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		return true;
	}
}
