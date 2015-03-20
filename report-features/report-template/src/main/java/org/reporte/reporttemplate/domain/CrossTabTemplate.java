package org.reporte.reporttemplate.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Entity implementation class for Entity: CrossTabTemplate
 *
 */
@Entity
@Table(name="crosstab_report_template")
@DiscriminatorValue("CrossTab")
public class CrossTabTemplate extends BaseReportTemplate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -664183739135513048L;

	public CrossTabTemplate() {
		super();
	}
   
	@ElementCollection(targetClass=CrossTabTemplateDetail.class)
	@CollectionTable(name="crosstab_template_details",
						joinColumns=@JoinColumn(name="crosstab_template_id",referencedColumnName="id"))
	private List<CrossTabTemplateDetail> crossTabDetail;

	public List<CrossTabTemplateDetail> getCrossTabDetail() {
		return crossTabDetail;
	}

	public void setCrossTabDetail(List<CrossTabTemplateDetail> crossTabDetail) {
		this.crossTabDetail = crossTabDetail;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("crossTabDetail",crossTabDetail)
		.toString();
	}
	
}
