package org.reporte.reporttemplate.dao;

import org.reporte.common.dao.BaseDAO;
import org.reporte.reporttemplate.dao.exception.ReportTemplateDAOException;
import org.reporte.reporttemplate.domain.BaseReportTemplate;

/**
 * 
 * Report Template DAO interface
 *
 */
public interface ReportTemplateDAO extends BaseDAO<BaseReportTemplate, ReportTemplateDAOException>{
	BaseReportTemplate updateEntity(BaseReportTemplate entity)	throws ReportTemplateDAOException;
}