package org.reportbay.report.domain;

import org.reportbay.common.domain.SqlTypeEnum;
import org.reportbay.datasource.domain.ColumnMetadata;
import org.reportbay.reporttemplate.domain.CrossTabFieldType;
import org.reportbay.reporttemplate.domain.GroupOrSum;

public class CrossTabAttribute {
	
	private GroupOrSum groupOrAggregate;
	private CrossTabFieldType fieldType;
	private int attributeDisplaySequence;
	private SqlTypeEnum type;
	private ColumnMetadata metaData;
	
	public GroupOrSum getGroupOrAggregate() {
		return groupOrAggregate;
	}
	public void setGroupOrAggregate(GroupOrSum groupOrAggregate) {
		this.groupOrAggregate = groupOrAggregate;
	}
	public CrossTabFieldType getFieldType() {
		return fieldType;
	}
	public void setFieldType(CrossTabFieldType fieldType) {
		this.fieldType = fieldType;
	}
	public int getAttributeDisplaySequence() {
		return attributeDisplaySequence;
	}
	public void setAttributeDisplaySequence(int attributeDisplaySequence) {
		this.attributeDisplaySequence = attributeDisplaySequence;
	}
	public SqlTypeEnum getType() {
		return type;
	}
	public void setType(SqlTypeEnum type) {
		this.type = type;
	}
	public ColumnMetadata getMetaData() {
		return metaData;
	}
	public void setMetaData(ColumnMetadata metaData) {
		this.metaData = metaData;
	}
	@Override
	public String toString() {
		return "CrossTabAttribute [groupOrAggregate=" + groupOrAggregate
				+ ", fieldType=" + fieldType + ", attributeDisplaySequence="
				+ attributeDisplaySequence + ", type=" + type + ", metaData="
				+ metaData + "]";
	}
}
