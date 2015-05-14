package org.reportbay.common.dao.impl;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.reportbay.common.dao.BaseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseDAOImpl<T1, T2 extends Throwable> implements BaseDAO<T1,T2>{

	private final Logger LOG = LoggerFactory.getLogger(BaseDAOImpl.class);
	/**
	 * @return the entityManager
	 */
	public abstract EntityManager getEntityManager() ;
	
	/**
	 * 
	 * @param msg
	 * @param e
	 * @return
	 */
	public abstract T2 createException(String msg, Throwable e);
	
	/**
	 * 
	 * @return class of entity
	 */
	public abstract Class<T1> getEntityClass();
	
	/**
	 * 
	 * @return findAll named query literal
	 */
	public abstract String getFindAllNamedQuery();

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public T1 insert(T1 entity) throws T2 {
		LOG.debug("create");
		
		try {
			getEntityManager().persist(entity);
		} 
		catch (PersistenceException e) {
			throw createException("Failed to persist entity", e);
		}
		
		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public T1 update(T1 entity) throws T2 {
		LOG.debug("update");
		try {
			return getEntityManager().merge(entity);
		} 
		catch (PersistenceException e) {
			throw createException("Failed to update entity.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void delete(T1 entity) throws T2 {
		LOG.debug("delete");
		try {
			getEntityManager().remove(getEntityManager().merge(entity));
		} 
		catch (PersistenceException e) {
			throw createException("Failed to delete entity .", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T1 find(int id) throws T2 {
		LOG.debug("find");
		try {
			return getEntityManager().find(getEntityClass(), id);
		} 
		catch (PersistenceException e) {
			throw createException("Failed to find entity.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T1> findAll() throws T2 {
		try {
			return getEntityManager().createNamedQuery(getFindAllNamedQuery(), getEntityClass()).getResultList();
		} 
		catch (PersistenceException e) {
			throw createException("Failed to find all entities.", e);
		}
	}
}