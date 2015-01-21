package org.reporte.model.dao.impl;

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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.reporte.common.interceptor.DAOLogger;
import org.reporte.common.interceptor.LogInterceptable;
import org.reporte.model.dao.ModelDAO;
import org.reporte.model.dao.exception.ModelDAOException;
import org.reporte.model.domain.AttributeMapping;
import org.reporte.datasource.domain.Datasource;
import org.reporte.model.domain.Model;
import org.reporte.model.domain.ModelQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Local(ModelDAO.class)
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@Interceptors(DAOLogger.class)
public class ModelDAOImpl implements ModelDAO, LogInterceptable<Model> {
	private final Logger LOG = LoggerFactory.getLogger(ModelDAOImpl.class);
	
	@PersistenceContext(unitName="reporte")
	private EntityManager em;
	
	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public Model insert(Model model) throws ModelDAOException {
		ModelQuery query = model.getQuery();
		try {
			// Below workaround is necessary as cascade operation doesn't work well with auto increment ID column.
			// id get generated only when the changes are flushed to database. Otherwise, it will keep setting 
			// 0 (zero) to FK id and thus causing it failed.
			
			model.setQuery(null);
			em.persist(model);
			em.flush();
			query.setId(model.getId());
			model.setQuery(query);
			em.merge(model);
		} 
		catch(PersistenceException e) {
			rollbackModelId(model);
			model.setQuery(query);
			throw new ModelDAOException("Failed to persist Model with given name[" + model.getName() + "].", e);
		}
		return model;
	}
	
	/**
	 * revert back id field of model and child records to 0 (uninitialized)
	 * @param model
	 */
	private void rollbackModelId(Model model){
		model.setId(0);
		
		List<AttributeMapping> attrList = model.getAttributeBindings();
		
		if(attrList!=null){
			for (AttributeMapping attributeMapping : attrList) {
				if(attributeMapping!=null){
					attributeMapping.setId(0);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void update(Model model) throws ModelDAOException {
		try {
			em.merge(model);
		} catch(PersistenceException e) {
			throw new ModelDAOException("Failed to update Model with given name[" + model.getName() + "].", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void delete(Model model) throws ModelDAOException {
		try {
			em.remove(em.merge(model));
		} catch(PersistenceException e) {
			throw new ModelDAOException("Failed to delete Model with given id[" + model.getId() + "].", e);
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Model find(int id) throws ModelDAOException {
		try {
			return em.find(Model.class, id);
		} catch(PersistenceException e) {
			throw new ModelDAOException("Failed to find Model with given id[" + id + "].", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Model> findAll() throws ModelDAOException {
		try {
			return em.createNamedQuery("Model.findAll", Model.class).getResultList();
		} catch(PersistenceException e) {
			throw new ModelDAOException("Failed to find all Models.", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<Model> findAllOrderByDatasourceName() throws ModelDAOException{
		
		try {
			// Get the criteria builder instance from entity manager
			final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			
			// create query indicate return result type as Model class
			CriteriaQuery<Model> criteriaQuery = criteriaBuilder.createQuery(Model.class);
			
			//query from Model table
			Root<Model> modelTable = criteriaQuery.from(Model.class);
			Root<Datasource> datasourceTable = criteriaQuery.from(Datasource.class);

			//select 
			criteriaQuery.select(modelTable);

			//where condition
			Predicate whereCond = criteriaBuilder.equal(modelTable.get("datasource").get("id"),
														datasourceTable.get("id"));
			criteriaQuery.where(whereCond);
			
			//order by datastore.name asc, model.name asc
			criteriaQuery.orderBy(criteriaBuilder.asc(datasourceTable.get("name")),
						  		  criteriaBuilder.asc(modelTable.get("name")));
			
			//converted to actual query
			final TypedQuery<Model> query = em.createQuery(criteriaQuery);
			
			return query.getResultList();
		} catch(PersistenceException e) {
			throw new ModelDAOException("Failed to find all Models ordered by datasource name, model name.", e);
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
