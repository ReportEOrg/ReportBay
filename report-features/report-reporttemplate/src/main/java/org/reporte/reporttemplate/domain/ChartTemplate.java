package org.reporte.reporttemplate.domain;

import java.lang.String;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 
 * JPA mapped super class for chart type template to keep common attribute
 *
 */
@MappedSuperclass
public class ChartTemplate extends BaseReportTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * chart title
	 */
	@Column(name="title")
	protected String title;
	/**
	 * 
	 */
	@Column(name="legend_ind")
	protected boolean showLegend;
	/**
	 * 
	 * @return 
	 */
	public String getTitle() {
	 	 return title; 
	}
	/**
	 * 
	 * @param title 
	 */
	public void setTitle(String title) { 
		 this.title = title; 
	}
	/**
	 * Getter of showLegend
	 */
	public boolean getShowLegend() {
	 	 return showLegend; 
	}
	/**
	 * 
	 * @param showLegend 
	 */
	public void setShowLegend(boolean showLegend) { 
		 this.showLegend = showLegend; 
	}
	/**
	 * 
	 * @return 
	 */
	public boolean isShowLegend() { 
		return showLegend;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object ref){
		
		if(!super.equals(ref)){
			return false;
		}
		ChartTemplate testRef = (ChartTemplate)ref;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		eb.append(title, testRef.title)
		  .append(showLegend, testRef.showLegend);
		
		return eb.isEquals();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int hashCode(){
		HashCodeBuilder hcb = new HashCodeBuilder(INITIAL_HASH, PRIME_HASH_MULTIPLIER); 
		
		hcb.appendSuper(super.hashCode())
		   .append(showLegend)
		   .append(title);
		
		return hcb.toHashCode();
	}

}
