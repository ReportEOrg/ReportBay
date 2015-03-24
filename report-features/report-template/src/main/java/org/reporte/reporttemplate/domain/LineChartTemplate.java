package org.reporte.reporttemplate.domain;

import static org.reporte.reporttemplate.domain.TemplateDiscriminatorConstants.LINE;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="cartesian_chart_template")
@DiscriminatorValue(LINE)
public class LineChartTemplate extends CartesianChartTemplate{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}