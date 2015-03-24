package org.reporte.reporttemplate.domain;

import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.reporte.common.domain.BaseJPAEntity;

/**
 * abstract base class for Report template entity bean using joined inheritance on template type
 * 
 *
 */
@Entity
@Table(name="report_template")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "template_type", discriminatorType = DiscriminatorType.STRING, length=1)

@NamedQueries({ @NamedQuery(name = "ReportTemplate.findAll", query = "SELECT e FROM BaseReportTemplate e") })
//defined field access due to use of transient
@Access(AccessType.FIELD)
public abstract class BaseReportTemplate extends BaseJPAEntity {

	private static final long serialVersionUID = 1L;
	
	@TableGenerator(name = "ReportTemplate_Gen", 
				    table = "id_gen", 
				    pkColumnName = "gen_name", pkColumnValue = "ReportTemplate_Gen", 
				    valueColumnName = "gen_val", allocationSize = 1)
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="ReportTemplate_Gen")
	private int id;

	/**
	 * 
	 */
	@Column(name="name")
	private String templateName;
	/**
	 * 
	 */
	@Column(name="model_id", nullable=false)
	private int modelId;
	/**
	 * 
	 */
	@Transient
	private ReportTemplateTypeEnum type;
	/**
	 * 
	 */
	@Column(name="report_display_name")
	private String reportDisplayName;
	
	/**
	 * this attribute is not persisted along with report template entity
	 */
	@Transient
	private ReportQuery reportQuery;
	
	/**
	 * 
	 * @return 
	 */
	public String getTemplateName() {
	 	 return templateName; 
	}
	/**
	 * 
	 * @param templateName 
	 */
	public void setTemplateName(String templateName) { 
		 this.templateName = templateName; 
	}
	/**
	 * 
	 * @return 
	 */
	public int getModelId() {
	 	 return modelId; 
	}
	/**
	 * 
	 * @param modelId 
	 */
	public void setModelId(int modelId) { 
		 this.modelId = modelId; 
	}
	/**
	 * 
	 * @return 
	 */
	public ReportTemplateTypeEnum getReportTemplateType() {
	 	 return type; 
	}
	/**
	 * 
	 * @param templateType 
	 */
	public void setReportTemplateType(ReportTemplateTypeEnum type) { 
		 this.type = type; 
	}

	/** wrapper method for entity manager to convert btw db to Enum pojo **/
	@Access(AccessType.PROPERTY)
	@Column(name="type")
	protected String getTypeCode(){
		if(type!=null){
			return type.getCode();
		}
		else{
			return null;
		}
	}
	
	protected void setTypeCode(String typeCode){
		ReportTemplateTypeEnum resultType = null;
		
		if(typeCode!=null){
			for(ReportTemplateTypeEnum refType: ReportTemplateTypeEnum.values()){
				if(refType.getCode().equals(typeCode)){
					resultType = refType;
					break;
				}
			}
		}
		type = resultType;
	}
	/**
	 * 
	 * @return 
	 */
	public String getReportDisplayName() {
	 	 return reportDisplayName; 
	}
	/**
	 * 
	 * @param reportDisplayName 
	 */
	public void setReportDisplayName(String reportDisplayName) { 
		 this.reportDisplayName = reportDisplayName; 
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
	
	//TODO: reconsider the need of override default equals as it may cause new BaseReportTemplate().equals(new BaseReportTemplate())
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object ref){

		if(!super.equals(ref)){
			return false;
		}
		
		BaseReportTemplate testRef = (BaseReportTemplate)ref;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		eb.append(id, testRef.id)
		  .append(modelId, testRef.modelId)
		  .append(templateName, testRef.templateName)
		  .append(reportDisplayName, testRef.reportDisplayName)
		  .append(type, testRef.type);
		
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
		   .append(modelId)
		   .append(templateName)
		   .append(reportDisplayName)
		   .append(type);
		
		return hcb.toHashCode();
	}
	/**
	 * @return the reportQuery
	 */
	public ReportQuery getReportQuery() {
		return reportQuery;
	}
	/**
	 * @param reportQuery the reportQuery to set
	 */
	public void setReportQuery(ReportQuery reportQuery) {
		this.reportQuery = reportQuery;
	}
	
	/**********************************
	 * due to the joined inheritence, oneToMany mapping has to be defined in the main joined table class
	 * although in db level the oneToMany target child table FK relationship is to the joined target table class
	 * 
	 *  class
	 *  ===========
	 *  BaseReportTemplate [report_template]  <--oneToMany -- templateSeries [template_series]
	 *     ^
	 *     |
	 *  ChartTemplate                   ------
	 *     ^                              ^
	 *     |                              |
	 *  CartesianChartTemplate    [cartesian_chart_template]
	 *    ^    ^   ^    ^                 |
	 *    |    |   |    |                 v
	 *  Bar  Area Line Column           -------
	 **********************************/
	@OneToMany(orphanRemoval=true, cascade =CascadeType.ALL , fetch=FetchType.EAGER)
	//uni-directional one to many, as search direction only from chart_template to template_series not the other way round
	@JoinColumn(name = "cart_chart_tmpt_id", referencedColumnName = "id", nullable=false)
	private Set<TemplateSeries> dataSeries;
	
	/**
	 * This method should only be used by class extends CartesianChartTemplate 
	 * @return 
	 */
	public Set<TemplateSeries> getDataSeries() {
	 	 return dataSeries; 
	}
	/**
	 * This method should only be used by class extends CartesianChartTemplate
	 * Setter of dataSeries
	 * @param dataSeries
	 */
	public void setDataSeries(Set<TemplateSeries> dataSeries) { 
		 this.dataSeries = dataSeries; 
	}
	
	/**********************************
	 * due to the joined inheritence, oneToMany mapping has to be defined in the main joined table class
	 * although in db level the oneToMany target child table FK relationship is to the joined target table class
	 * 
	 *  class
	 *  ===========
	 *  BaseReportTemplate [report_template]  <--oneToMany -- CrossTabTemplateGroup [xtab_template_grp]
	 *     ^
	 *     |
	 *  CrossTabTemplate    [xtab_template]
	 **********************************/
//	@OneToMany(orphanRemoval=true, cascade =CascadeType.ALL , fetch=FetchType.EAGER)
//	//uni-directional one to many, as search direction only from xtab_template to xtab_template_grp not the other way round
//	@JoinColumn(name = "xtab_tmpt_id", referencedColumnName = "id", nullable=false)
//	private Set<CrossTabTemplateGroup> crossTabTemplateGroups;
//
//	/**
//	 * This method should only be used by class CrossTabTemplate 
//	 * @return the crossTabTemplateGroups
//	 */
//	public Set<CrossTabTemplateGroup> getCrossTabTemplateGroups() {
//		return crossTabTemplateGroups;
//	}
//	/**
//	 * This method should only be used by class CrossTabTemplate
//	 * @param crossTabTemplateGroups the crossTabTemplateGroups to set
//	 */
//	public void setCrossTabTemplateGroups(Set<CrossTabTemplateGroup> crossTabTemplateGroups) {
//		this.crossTabTemplateGroups = crossTabTemplateGroups;
//	}
}
