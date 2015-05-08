package org.reportbay.snapshot.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.reportbay.common.domain.BaseJPAEntity;

@MappedSuperclass
public class ReportSnapShotBase extends BaseJPAEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@TableGenerator(name = "ReportSnapShot_Gen", table = "id_gen", pkColumnName = "gen_name", 
					valueColumnName = "gen_val", pkColumnValue = "ReportSnapShot_Gen", 
					allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="ReportSnapShot_Gen")
	private int id;
	
	@Column(name="creation_date", length=19)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	@Column(name="template_type", length=1)
	private char templateType;
	
	@Column(name="template_id", length=11)
	private int templateId;
	
	@Column(name="report_name", length=255)
	private String reportName;
	
	public ReportSnapShotBase(){};
	/**
	 * 
	 * @param id
	 * @param creationDate
	 * @param templateType
	 * @param reportName
	 */
	public ReportSnapShotBase(int id, Date creationDate, char templateType, int templateId, String reportName){
		this.id = id;
		this.creationDate = creationDate;
		this.templateType = templateType;
		this.templateId = templateId;
		this.reportName = reportName;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the templateType
	 */
	public char getTemplateType() {
		return templateType;
	}

	/**
	 * @param templateType the templateType to set
	 */
	public void setTemplateType(char templateType) {
		this.templateType = templateType;
	}

	/**
	 * @return the reportName
	 */
	public String getReportName() {
		return reportName;
	}

	/**
	 * @param reportName the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	/**
	 * @return the templateId
	 */
	public int getTemplateId() {
		return templateId;
	}
	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object ref){

		if(!super.equals(ref)){
			return false;
		}
		
		ReportSnapShotBase testRef = (ReportSnapShotBase)ref;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		eb.append(id, testRef.id)
		  .append(creationDate, testRef.creationDate)
		  .append(templateType, testRef.templateType)
		  .append(templateId, testRef.templateId)
		  .append(reportName, testRef.reportName);
		
		return eb.isEquals();
	}
	//TODO: reconsider the need of override default equals as it may cause all new instance of BaseReportTemplate having the same hashCode
	// this will lead to Set interface object's add of 2 new instance results in single instance stored in set
	//
	/**
	 * {@inheritDoc}
	 */
	public int hashCode(){
		
		HashCodeBuilder hcb = new HashCodeBuilder(INITIAL_HASH, PRIME_HASH_MULTIPLIER);
		
		hcb.appendSuper(super.hashCode())
		   .append(id)
		   .append(creationDate)
		   .append(templateType)
		   .append(templateId)
		   .append(reportName);
		
		return hcb.toHashCode();
	}

}