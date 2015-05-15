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
import org.reportbay.datasource.dao.DatabaseTypeDAO;
import org.reportbay.datasource.dao.exception.DatabaseTypeDAOException;
import org.reportbay.datasource.domain.DatabaseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Local(DatabaseTypeDAO.class)
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@Interceptors(DAOLogger.class)
public class DatabaseTypeDAOImpl extends BaseDAOImpl<DatabaseType,DatabaseTypeDAOException> 
	implements DatabaseTypeDAO, LogInterceptable<DatabaseType> {

	private final Logger LOG = LoggerFactory.getLogger(DatabaseTypeDAOImpl.class);

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
	public DatabaseTypeDAOException createException(String msg, Throwable e) {
		return new DatabaseTypeDAOException(msg, e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<DatabaseType> getEntityClass() {
		return DatabaseType.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFindAllNamedQuery() {
		return "DatabaseType.findAll";
	}
}