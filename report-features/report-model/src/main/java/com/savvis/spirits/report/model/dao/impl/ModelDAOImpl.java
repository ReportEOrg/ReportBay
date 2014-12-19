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
import com.savvis.spirits.report.model.dao.ModelDAO;
import com.savvis.spirits.report.model.dao.ModelDAOException;
import com.savvis.spirits.report.model.domain.Model;
import com.savvis.spirits.report.model.domain.ModelQuery;

@Local(ModelDAO.class)
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@Interceptors(DAOLogger.class)
public class ModelDAOImpl implements ModelDAO, LogInterceptable<Model> {
	private final Logger LOG = LoggerFactory.getLogger(ModelDAOImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public Model insert(Model model) throws ModelDAOException {
		try {
			// Below workaround is necessary as cascade operation doesn't work well with auto increment ID column.
			// id get generated only when the changes are flushed to database. Otherwise, it will keep setting 
			// 0 (zero) to FK id and thus causing it failed.
			ModelQuery query = model.getQuery();
			model.setQuery(null);
			em.persist(model);
			em.flush();
			query.setId(model.getId());
			model.setQuery(query);
			em.merge(model);
		} catch(PersistenceException e) {
			throw new ModelDAOException("Failed to persist Model with given name[" + model.getName() + "].", e);
		}
		return model;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void update(Model model) throws ModelDAOException {
		try {
			em.merge(model);
		} catch(PersistenceException e) {
			throw new ModelDAOException("Failed to update Model with given name[" + model.getName() + "].", e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void delete(Model model) throws ModelDAOException {
		try {
			em.remove(em.merge(model));
		} catch(PersistenceException e) {
			throw new ModelDAOException("Failed to delete Model with given id[" + model.getId() + "].", e);
		}
		
	}

	@Override
	public Model find(int id) throws ModelDAOException {
		try {
			return em.find(Model.class, id);
		} catch(PersistenceException e) {
			throw new ModelDAOException("Failed to find Model with given id[" + id + "].", e);
		}
	}

	@Override
	public List<Model> findAll() throws ModelDAOException {
		try {
			return em.createNamedQuery("Model.findAll", Model.class).getResultList();
		} catch(PersistenceException e) {
			throw new ModelDAOException("Failed to find all Models.", e);
		}
	}

	@Override
	public Logger getLogger() {
		return LOG;
	}
	
}
