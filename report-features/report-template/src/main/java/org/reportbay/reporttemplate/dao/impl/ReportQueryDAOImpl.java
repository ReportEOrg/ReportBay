package org.reportbay.reporttemplate.dao.impl;

import java.util.List;

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
import org.reportbay.reporttemplate.dao.ReportQueryDAO;
import org.reportbay.reporttemplate.dao.exception.ReportQueryDAOException;
import org.reportbay.reporttemplate.domain.ReportQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//local business interface
@Local(ReportQueryDAO.class)
//stateless session bean
@Stateless
//container managed trancaction manager
@TransactionManagement(TransactionManagementType.CONTAINER)
@Interceptors(DAOLogger.class)
public class ReportQueryDAOImpl extends BaseDAOImpl<ReportQuery, ReportQueryDAOException> 
	implements ReportQueryDAO, LogInterceptable<ReportQuery>{

	private final Logger LOG = LoggerFactory.getLogger(ReportQueryDAOImpl.class);
	
	@PersistenceContext(unitName="reportbay")
	private EntityManager em;
	
	/**
	 * as the find method will be used in transaction context, added required transaction attribute type
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public ReportQuery find(int id) throws ReportQueryDAOException {
		try {
			return super.find(id);
		}
		catch (PersistenceException e) {
			throw new ReportQueryDAOException("Failed finding Report query "+id, e);
		}
	}
	/**
	 * not supported. always return null
	 */
	@Override
	public List<ReportQuery> findAll() throws ReportQueryDAOException {
		return null;
	}

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
	public EntityManager getEntityManager() {
		return em;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReportQueryDAOException createException(String msg, Throwable e) {
		return new ReportQueryDAOException(msg, e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<ReportQuery> getEntityClass() {
		return ReportQuery.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFindAllNamedQuery() {
		//find all not supported, so passed null 
		return null;
	}
}