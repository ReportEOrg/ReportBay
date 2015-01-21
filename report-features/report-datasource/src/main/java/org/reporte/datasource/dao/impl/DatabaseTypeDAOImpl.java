package org.reporte.datasource.dao.impl;

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

import org.reporte.common.interceptor.DAOLogger;
import org.reporte.common.interceptor.LogInterceptable;
import org.reporte.datasource.dao.DatabaseTypeDAO;
import org.reporte.datasource.dao.exception.DatabaseTypeDAOException;
import org.reporte.datasource.domain.DatabaseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Local(DatabaseTypeDAO.class)
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@Interceptors(DAOLogger.class)
public class DatabaseTypeDAOImpl implements DatabaseTypeDAO, LogInterceptable<DatabaseType> {
	private final Logger LOG = LoggerFactory.getLogger(DatabaseTypeDAOImpl.class);

	@PersistenceContext(unitName="reporte")
	private EntityManager em;

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public DatabaseType insert(DatabaseType databaseType) throws DatabaseTypeDAOException {
		try {
			em.persist(databaseType);
		} catch (PersistenceException e) {
			throw new DatabaseTypeDAOException("Failed to persist DatabaseType with given name[" + databaseType.getName() + "].", e);
		}
		return databaseType;
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void update(DatabaseType databaseType) throws DatabaseTypeDAOException {
		try {
			em.merge(databaseType);
		} catch (PersistenceException e) {
			throw new DatabaseTypeDAOException("Failed to update DatabaseType with given name[" + databaseType.getName() + "].", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void delete(DatabaseType databaseType) throws DatabaseTypeDAOException {
		try {
			em.remove(em.merge(databaseType));
		} catch (PersistenceException e) {
			throw new DatabaseTypeDAOException("Failed to delete DatabaseType with given id[" + databaseType.getId() + "].", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DatabaseType find(int id) throws DatabaseTypeDAOException {
		try {
			return em.find(DatabaseType.class, id);
		} catch (PersistenceException e) {
			throw new DatabaseTypeDAOException("Failed to find DatabaseType with given id[" + id + "].", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DatabaseType> findAll() throws DatabaseTypeDAOException {
		try {
			return em.createNamedQuery("DatabaseType.findAll", DatabaseType.class).getResultList();
		} catch (PersistenceException e) {
			throw new DatabaseTypeDAOException("Failed to get all DatabaseTypes.", e);
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