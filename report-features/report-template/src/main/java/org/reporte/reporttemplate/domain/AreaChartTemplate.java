package org.reporte.reporttemplate.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="cartesian_chart_template")
@DiscriminatorValue("A")
public class AreaChartTemplate extends CartesianChartTemplate{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}