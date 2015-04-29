package org.reportbay.reporttemplate.domain;

import static org.reportbay.reporttemplate.domain.TemplateDiscriminatorConstants.BAR;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="cartesian_chart_template")
@DiscriminatorValue(BAR)
public class BarChartTemplate extends CartesianChartTemplate{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}