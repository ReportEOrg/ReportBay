package org.reportbay.reporttemplate.dao.impl;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.reportbay.common.dao.impl.BaseDAOImpl;
import org.reportbay.common.interceptor.DAOLogger;
import org.reportbay.common.interceptor.LogInterceptable;
import org.reportbay.reporttemplate.dao.ReportTemplateDAO;
import org.reportbay.reporttemplate.dao.exception.ReportTemplateDAOException;
import org.reportbay.reporttemplate.domain.BaseReportTemplate;
import org.reportbay.reporttemplate.domain.CartesianChartTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//local business interface
@Local(ReportTemplateDAO.class)
//stateless session bean
@Stateless
//container managed trancaction manager
@TransactionManagement(TransactionManagementType.CONTAINER)
@Interceptors(DAOLogger.class)
public class ReportTemplateDAOImpl extends BaseDAOImpl<BaseReportTemplate, ReportTemplateDAOException>
	implements ReportTemplateDAO, LogInterceptable<BaseReportTemplate>{

	private final Logger LOG = LoggerFactory.getLogger(ReportTemplateDAOImpl.class);
	
	@PersistenceContext(unitName="reportbay")
	private EntityManager em;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Logger getLogger() {
		return LOG;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public BaseReportTemplate insert(BaseReportTemplate entity)
			throws ReportTemplateDAOException {
		try {

			processJoinTableChildRelationship(entity);

			em.persist(entity);
		} catch (PersistenceException e) {
			throw new ReportTemplateDAOException("Failed to persist ReportTemplate with given name[" + entity.getTemplateName() + "].", e);
		}
		return entity;
	}
	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public BaseReportTemplate update(BaseReportTemplate entity)	throws ReportTemplateDAOException {
		try {
			processJoinTableChildRelationship(entity);
			
			return em.merge(entity);
			
		} catch (PersistenceException e) {
			throw new ReportTemplateDAOException("Failed to update ReportTemplate with given name[" + entity.getTemplateName() + "].", e);
		}
		
	}

	/**
	 * pre processing of the join table's child relationship which is link at main entity class due to JPA limitation
	 * 
	 * @param entity
	 */
	private void processJoinTableChildRelationship(BaseReportTemplate entity){
		
		//non cartesian chart type template, explicit remove data series to prevent accidently set
		if(!(entity instanceof CartesianChartTemplate)){
			if(entity.getDataSeries()!=null){
				entity.getDataSeries().clear();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityManager getEntityManager() {
		return em;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReportTemplateDAOException createException(String msg, Throwable e) {
		return new ReportTemplateDAOException(msg,e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<BaseReportTemplate> getEntityClass() {
		return BaseReportTemplate.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFindAllNamedQuery() {
		return "ReportTemplate.findAll";
	}
}