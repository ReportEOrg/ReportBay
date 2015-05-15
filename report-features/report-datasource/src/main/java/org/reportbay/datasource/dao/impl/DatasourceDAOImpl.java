package org.reportbay.datasource.dao.impl;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.reportbay.common.dao.impl.BaseDAOImpl;
import org.reportbay.common.interceptor.DAOLogger;
import org.reportbay.common.interceptor.LogInterceptable;
import org.reportbay.datasource.dao.DatasourceDAO;
import org.reportbay.datasource.dao.exception.DatasourceDAOException;
import org.reportbay.datasource.domain.Datasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Local(DatasourceDAO.class)
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@Interceptors(DAOLogger.class)
public class DatasourceDAOImpl extends BaseDAOImpl<Datasource, DatasourceDAOException> 
	implements DatasourceDAO, LogInterceptable<Datasource> {

	private final Logger LOG = LoggerFactory.getLogger(DatasourceDAOImpl.class);

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
	@Override
	public EntityManager getEntityManager() {
		return em;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DatasourceDAOException createException(String msg, Throwable e) {
		return new DatasourceDAOException(msg,e);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<Datasource> getEntityClass() {
		return Datasource.class;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFindAllNamedQuery() {
		return "Datasource.findAll";
	}
}
