package org.reportbay.reporttemplate.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.reportbay.common.domain.BaseJPAEntity;
import org.reportbay.common.domain.SqlTypeEnum;

/**
 * @author ahamed.nijamudeen
 * @version 1.0
 * @since 01-Feb-2015
 *
 */
@Embeddable
public class CrossTabTemplateDetail extends BaseJPAEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1966934340275786589L;
		
	@Column(name="model_attribute_name",nullable=false)
	private String modelAttributeName;
	@Column(name="attribute_display_name")
	private String attributeDisplayName;
	@Column(name="field_type",nullable=false)
	@Enumerated(EnumType.STRING)
	private CrossTabFieldType fieldType;
	@Column(name="sql_type",nullable=false)
	@Enumerated(EnumType.STRING)
	private SqlTypeEnum sqltype;
	@Column(name="sql_function",nullable=false)
	@Enumerated(EnumType.STRING)
	private SqlFunction sqlFunction;
	@Column(name="attribute_display_sequence",nullable=false)
	private int attributeDisplaySequence;
	@Column(name="group_or_aggregate",nullable=false)
	@Enumerated(EnumType.STRING)
	private GroupOrAggregate groupOrAggregate;
	
	public CrossTabTemplateDetail(){
		super();
	}
	
	
	public CrossTabTemplateDetail(String modelAttributeName,
			String attributeDisplayName, CrossTabFieldType fieldType,
			SqlTypeEnum sqltype, SqlFunction sqlFunction,
			int attributeDisplaySequence, GroupOrAggregate groupOrAggregate) {
		super();
		this.modelAttributeName = modelAttributeName;
		this.attributeDisplayName = attributeDisplayName;
		this.fieldType = fieldType;
		this.sqltype = sqltype;
		this.sqlFunction = sqlFunction;
		this.attributeDisplaySequence = attributeDisplaySequence;
		this.groupOrAggregate = groupOrAggregate;
	}


	public String getModelAttributeName() {
		return modelAttributeName;
	}
	public void setModelAttributeName(String modelAttributeName) {
		this.modelAttributeName = modelAttributeName;
	}
	public String getAttributeDisplayName() {
		return attributeDisplayName;
	}
	public void setAttributeDisplayName(String attributeDisplayName) {
		this.attributeDisplayName = attributeDisplayName;
	}
	public CrossTabFieldType getFieldType() {
		return fieldType;
	}
	public void setFieldType(CrossTabFieldType fieldType) {
		this.fieldType = fieldType;
	}
	public SqlTypeEnum getSqltype() {
		return sqltype;
	}
	public void setSqltype(SqlTypeEnum sqltype) {
		this.sqltype = sqltype;
	}

	public SqlFunction getSqlFunction() {
		return sqlFunction;
	}
	
	public void setSqlFunction(SqlFunction sqlFunction) {
		this.sqlFunction = sqlFunction;
	}
	public int getAttributeDisplaySequence() {
		return attributeDisplaySequence;
	}
	public void setAttributeDisplaySequence(int attributeDisplaySequence) {
		this.attributeDisplaySequence = attributeDisplaySequence;
	}
	public GroupOrAggregate getGroupOrAggregate() {
		return groupOrAggregate;
	}


	public void setGroupOrAggregate(GroupOrAggregate groupOrAggregate) {
		this.groupOrAggregate = groupOrAggregate;
	}


	@Override
	public int hashCode() {
		return new HashCodeBuilder(INITIAL_HASH, PRIME_HASH_MULTIPLIER)
		.append(modelAttributeName)
		.append(attributeDisplayName)
		.append(fieldType)
		.append(sqltype)
		.append(sqlFunction)
		.append(attributeDisplaySequence)
		.append(groupOrAggregate)
		.toHashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CrossTabTemplateDetail))
			return false;
		final CrossTabTemplateDetail other = (CrossTabTemplateDetail) obj;
		return new EqualsBuilder()
		.append(modelAttributeName,other.modelAttributeName)
		.append(attributeDisplayName,other.attributeDisplayName)
		.append(fieldType,other.fieldType)
		.append(sqltype,other.sqltype)
		.append(sqlFunction,other.sqlFunction)
		.append(attributeDisplaySequence,other.attributeDisplaySequence)
		.append(groupOrAggregate, other.groupOrAggregate)
		.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("modelAttributeName",modelAttributeName)
		.append("attributeDisplayName",attributeDisplayName)
		.append("fieldType",fieldType)
		.append("sqltype",sqltype)
		.append("sqlFunction",sqlFunction)
		.append("attributeDisplaySequence",attributeDisplaySequence)
		.append("groupOrAggregate", groupOrAggregate)
		.toString();
	}
}
