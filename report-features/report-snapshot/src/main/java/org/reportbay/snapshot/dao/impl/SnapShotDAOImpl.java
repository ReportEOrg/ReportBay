package org.reportbay.snapshot.dao.impl;

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
import org.reportbay.snapshot.dao.SnapShotDAO;
import org.reportbay.snapshot.dao.exception.SnapShotDAOException;
import org.reportbay.snapshot.domain.ReportSnapShot;
import org.reportbay.snapshot.domain.ReportSnapShotBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Local(SnapShotDAO.class)
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@Interceptors(DAOLogger.class)
public class SnapShotDAOImpl implements SnapShotDAO, LogInterceptable<ReportSnapShot>{
	
	private final Logger LOG = LoggerFactory.getLogger(SnapShotDAOImpl.class);
	
	@PersistenceContext(unitName="reportbay")
	private EntityManager em;

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public ReportSnapShot insert(ReportSnapShot entity) throws SnapShotDAOException {
		LOG.debug("create");
		
		try {
			em.persist(entity);
		} 
		catch (PersistenceException e) {
			throw new SnapShotDAOException("Failed to persist snapshot", e);
		}
		
		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void update(ReportSnapShot entity) throws SnapShotDAOException {
		LOG.debug("update");
		try {
			em.merge(entity);
		} 
		catch (PersistenceException e) {
			throw new SnapShotDAOException("Failed to update snap shot.", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public ReportSnapShot updateEntity(ReportSnapShot entity) throws SnapShotDAOException {
		LOG.debug("update");
		try {
			return em.merge(entity);
		} 
		catch (PersistenceException e) {
			throw new SnapShotDAOException("Failed to update snap shot.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void delete(ReportSnapShot entity) throws SnapShotDAOException {
		
		try {
			em.remove(em.merge(entity));
		} 
		catch (PersistenceException e) {
			throw new SnapShotDAOException("Failed to delete snapshot with given id[" + entity.getId() + "].", e);
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReportSnapShot find(int id) throws SnapShotDAOException {
		
		try {
			return em.find(ReportSnapShot.class, id);
		} 
		catch (PersistenceException e) {
			throw new SnapShotDAOException("Failed to find snapshot with given id[" + id + "].", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ReportSnapShot> findAll() throws SnapShotDAOException {
		try {
			return em.createNamedQuery("ReportSnapShot.findAll", ReportSnapShot.class).getResultList();
		} 
		catch (PersistenceException e) {
			throw new SnapShotDAOException("Failed to find all snapshot.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ReportSnapShotBase> findAllBase() throws SnapShotDAOException{
		try {
			return em.createNamedQuery("ReportSnapShot.findAllBase", ReportSnapShotBase.class).getResultList();
		} 
		catch (PersistenceException e) {
			throw new SnapShotDAOException("Failed to find all snapshot.", e);
		}
	}
	
	@Override
	public Logger getLogger() {
		return LOG;
	}
}