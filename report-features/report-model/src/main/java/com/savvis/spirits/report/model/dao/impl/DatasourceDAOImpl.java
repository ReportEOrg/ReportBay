package com.savvis.spirits.report.model.dao.impl;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.savvis.spirits.report.common.interceptor.DAOLogger;
import com.savvis.spirits.report.common.interceptor.LogInterceptable;
import com.savvis.spirits.report.model.dao.DatasourceDAO;
import com.savvis.spirits.report.model.dao.DatasourceDAOException;
import com.savvis.spirits.report.model.domain.Datasource;

@Local(DatasourceDAO.class)
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@Interceptors(DAOLogger.class)
public class DatasourceDAOImpl implements DatasourceDAO,
		LogInterceptable<Datasource> {
	private final Logger LOG = LoggerFactory.getLogger(DatasourceDAOImpl.class);

	@PersistenceContext
	private EntityManager em;

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

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void update(Datasource datasource) throws DatasourceDAOException {
		try {
			em.merge(datasource);
		} catch (PersistenceException e) {
			throw new DatasourceDAOException("Failed to update Datasource with given name[" + datasource.getName() + "].", e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void delete(Datasource datasource) throws DatasourceDAOException {
		try {
			em.remove(em.merge(datasource));
		} catch (PersistenceException e) {
			throw new DatasourceDAOException("Failed to delete Datasource with given id[" + datasource.getId() + "].", e);
		}
	}

	@Override
	public Datasource find(int id) throws DatasourceDAOException {
		try {
			return em.find(Datasource.class, id);
		} catch (PersistenceException e) {
			throw new DatasourceDAOException("Failed to find Datasource with given id[" + id + "].", e);
		}
	}

	@Override
	public List<Datasource> findAll() throws DatasourceDAOException {
		try {
			return em.createNamedQuery("Datasource.findAll", Datasource.class).getResultList();
		} catch (PersistenceException e) {
			throw new DatasourceDAOException("Failed to find all Datasources.", e);
		}
	}

	@Override
	public Logger getLogger() {
		return LOG;
	}

}
