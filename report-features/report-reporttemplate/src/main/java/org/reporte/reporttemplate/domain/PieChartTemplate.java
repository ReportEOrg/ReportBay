package org.reporte.reporttemplate.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name="pie_chart_template")
@DiscriminatorValue("P")
//defined field access due to use of transient
@Access(AccessType.FIELD)
public class PieChartTemplate extends ChartTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	@Column(name="category_name")
	private String categoryName;
	/**
	 * 
	 */
	@Column(name="model_category_field")
	private String modelCategoryField;
	/**
	 * 
	 */
	@Column(name="model_data_field")
	private String modelDataField;
	/**
	 * 
	 */
	@Column(name="data_label_ind")
	private boolean showDataLabel;
	/**
	 * 
	 */
	@Transient
	private PieChartDataTypeEnum dataTypeFormat;
	/**
	 * 
	 * @return 
	 */
	public String getCategoryName() {
	 	 return categoryName; 
	}
	/**
	 * 
	 * @param categoryName 
	 */
	public void setCategoryName(String categoryName) { 
		 this.categoryName = categoryName; 
	}
	/**
	 * Getter of modelCatagoryField
	 */
	public String getModelCategoryField() {
	 	 return modelCategoryField; 
	}
	/**
	 * Setter of modelCatagoryField
	 */
	public void setModelCategoryField(String modelCategoryField) { 
		 this.modelCategoryField = modelCategoryField; 
	}
	/**
	 * 
	 * @return 
	 */
	public String getModelDataField() {
	 	 return modelDataField; 
	}
	/**
	 * 
	 * @param modelDataField 
	 */
	public void setModelDataField(String modelDataField) { 
		 this.modelDataField = modelDataField; 
	}
	/**
	 * Getter of showDataLabel
	 */
	public boolean getShowDataLabel() {
	 	 return showDataLabel; 
	}
	/**
	 * 
	 * @param showDataLabel 
	 */
	public void setShowDataLabel(boolean showDataLabel) { 
		 this.showDataLabel = showDataLabel; 
	}
	/**
	 * 
	 * @return 
	 */
	public PieChartDataTypeEnum getDataTypeFormat() {
	 	 return dataTypeFormat; 
	}
	/**
	 * 
	 * @param dataTypeFormat 
	 */
	public void setDataTypeFormat(PieChartDataTypeEnum dataTypeFormat) { 
		 this.dataTypeFormat = dataTypeFormat; 
	}
	
	/** wrapper method for convert db short code to enum **/
	@Access(AccessType.PROPERTY)
	@Column(name="data_type_format")
	protected String getDataTypeFormatCode(){
		if(dataTypeFormat!=null){
			return dataTypeFormat.getCode();
		}
		else{
			return null;
		}
	}
	
	protected void setDataTypeFormatCode(String dataTypeFormatCode){
		PieChartDataTypeEnum resultType = null;
		
		if(dataTypeFormatCode!=null){
			for(PieChartDataTypeEnum refType: PieChartDataTypeEnum.values()){
				if(refType.getCode().equals(dataTypeFormatCode)){
					resultType = refType;
					break;
				}
			}
		}
		dataTypeFormat = resultType;
	}
	/**
	 * 
	 * @return 
	 */
	public boolean isShowDataLabel() { 
		return showDataLabel;
	} 

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object ref){
		if(!super.equals(ref)){
			return false;
		}
		
		PieChartTemplate testRef = (PieChartTemplate)ref;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		eb.append(categoryName, testRef.categoryName)
		  .append(dataTypeFormat, testRef.dataTypeFormat)
		  .append(modelCategoryField, testRef.modelCategoryField)
		  .append(modelDataField, testRef.modelDataField)
		  .append(showDataLabel, testRef.showDataLabel);
		  
		return eb.isEquals();
	}
	/**
	 * {@inheritDoc}
	 */
	public int hashCode(){
		
		HashCodeBuilder hcb = new HashCodeBuilder(super.hashCode(), PRIME_HASH_MULTIPLIER);
		
		hcb.append(categoryName)
		   .append(dataTypeFormat)
		   .append(modelCategoryField)
		   .append(modelDataField)
		   .append(showDataLabel);
		
		return hcb.toHashCode();
	}
}
