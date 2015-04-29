package org.reportbay.reporttemplate.dao;

import org.reportbay.common.dao.BaseDAO;
import org.reportbay.reporttemplate.dao.exception.ReportTemplateDAOException;
import org.reportbay.reporttemplate.domain.BaseReportTemplate;

/**
 * 
 * Report Template DAO interface
 *
 */
public interface ReportTemplateDAO extends BaseDAO<BaseReportTemplate, ReportTemplateDAOException>{
	BaseReportTemplate updateEntity(BaseReportTemplate entity)	throws ReportTemplateDAOException;
}