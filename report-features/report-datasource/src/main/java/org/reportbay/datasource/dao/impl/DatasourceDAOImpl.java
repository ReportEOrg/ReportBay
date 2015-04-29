package org.reportbay.datasource.dao.impl;

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
public class DatasourceDAOImpl implements DatasourceDAO,
		LogInterceptable<Datasource> {
	private final Logger LOG = LoggerFactory.getLogger(DatasourceDAOImpl.class);

	@PersistenceContext(unitName="reportbay")
	private EntityManager em;

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public Datasource insert(Datasource datasource) throws DatasourceDAOException {
		try {
			em.persist(datasource);
		} catch (PersistenceException e) {
			throw new DatasourceDAOException("Failed to persist Datasource with given name[" + datasource.getName() + "].", e);
		}
		return datasource;
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void update(Datasource datasource) throws DatasourceDAOException {
		try {
			em.merge(datasource);
		} catch (PersistenceException e) {
			throw new DatasourceDAOException("Failed to update Datasource with given name[" + datasource.getName() + "].", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void delete(Datasource datasource) throws DatasourceDAOException {
		try {
			em.remove(em.merge(datasource));
		} catch (PersistenceException e) {
			throw new DatasourceDAOException("Failed to delete Datasource with given id[" + datasource.getId() + "].", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Datasource find(int id) throws DatasourceDAOException {
		try {
			return em.find(Datasource.class, id);
		} catch (PersistenceException e) {
			throw new DatasourceDAOException("Failed to find Datasource with given id[" + id + "].", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Datasource> findAll() throws DatasourceDAOException {
		try {
			return em.createNamedQuery("Datasource.findAll", Datasource.class).getResultList();
		} catch (PersistenceException e) {
			throw new DatasourceDAOException("Failed to find all Datasources.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Logger getLogger() {
		return LOG;
	}

}
