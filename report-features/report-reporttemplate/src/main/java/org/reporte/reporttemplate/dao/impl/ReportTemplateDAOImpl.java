package org.reporte.reporttemplate.dao.impl;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.reporte.common.interceptor.DAOLogger;
import org.reporte.common.interceptor.LogInterceptable;
import org.reporte.reporttemplate.dao.ReportTemplateDAO;
import org.reporte.reporttemplate.dao.exception.ReportTemplateDAOException;
import org.reporte.reporttemplate.domain.BaseReportTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//local business interface
@Local(ReportTemplateDAO.class)
//stateless session bean
@Stateless
//container managed trancaction manager
@TransactionManagement(TransactionManagementType.CONTAINER)
@Interceptors(DAOLogger.class)
public class ReportTemplateDAOImpl implements ReportTemplateDAO, LogInterceptable<BaseReportTemplate>{

	private final Logger LOG = LoggerFactory.getLogger(ReportTemplateDAOImpl.class);
	
	@PersistenceContext
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
	@Override
	public BaseReportTemplate insert(BaseReportTemplate entity)
			throws ReportTemplateDAOException {
		try {
			em.persist(entity);
		} catch (PersistenceException e) {
			throw new ReportTemplateDAOException("Failed to persist ReportTemplate with given name[" + entity.getTemplateName() + "].", e);
		}
		return entity;
	}

	@Override
	public void update(BaseReportTemplate entity)
			throws ReportTemplateDAOException {
		try {
			em.merge(entity);
			
		} catch (PersistenceException e) {
			throw new ReportTemplateDAOException("Failed to update ReportTemplate with given name[" + entity.getTemplateName() + "].", e);
		}
		
	}
	
	public BaseReportTemplate updateEntity(BaseReportTemplate entity)	throws ReportTemplateDAOException {
		try {
			return em.merge(entity);
			
		} catch (PersistenceException e) {
			throw new ReportTemplateDAOException("Failed to update ReportTemplate with given name[" + entity.getTemplateName() + "].", e);
		}
		
	}

	@Override
	public void delete(BaseReportTemplate entity)
			throws ReportTemplateDAOException {
		try {
			em.remove(em.merge(entity));
		} catch (PersistenceException e) {
			throw new ReportTemplateDAOException("Failed to delete ReportTemplate with given id[" + entity.getId() + "].", e);
		}
		
	}

	@Override
	public BaseReportTemplate find(int id) throws ReportTemplateDAOException {
		try {
			return em.find(BaseReportTemplate.class, id);
		} catch (PersistenceException e) {
			throw new ReportTemplateDAOException("Failed to find ReportTemplate with given id[" + id + "].", e);
		}
	}

	@Override
	public List<BaseReportTemplate> findAll() throws ReportTemplateDAOException {
		try {
			return em.createNamedQuery("ReportTemplate.findAll", BaseReportTemplate.class).getResultList();
		} catch (PersistenceException e) {
			throw new ReportTemplateDAOException("Failed to find all ReportTemplate.", e);
		}
	}


	
}