package org.reporte.reporttemplate.dao.impl;

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
import org.reporte.reporttemplate.dao.ReportQueryDAO;
import org.reporte.reporttemplate.dao.exception.ReportQueryDAOException;
import org.reporte.reporttemplate.domain.ReportQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//local business interface
@Local(ReportQueryDAO.class)
//stateless session bean
@Stateless
//container managed trancaction manager
@TransactionManagement(TransactionManagementType.CONTAINER)
@Interceptors(DAOLogger.class)
public class ReportQueryDAOImpl implements ReportQueryDAO, LogInterceptable<ReportQuery>{

	private final Logger LOG = LoggerFactory.getLogger(ReportQueryDAOImpl.class);
	
	@PersistenceContext(unitName="reporte")
	private EntityManager em;
	
	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public ReportQuery insert(ReportQuery entity) throws ReportQueryDAOException {

		try {
			em.persist(entity);
		}
		catch (PersistenceException e) {
			throw new ReportQueryDAOException("Failed creating Report query "+entity.getId(), e);
		}
		
		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void update(ReportQuery entity) throws ReportQueryDAOException {
		try {
			em.merge(entity);
		}
		catch(PersistenceException e){
			throw new ReportQueryDAOException("Failed updating Report query "+entity.getId(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void delete(ReportQuery entity) throws ReportQueryDAOException {
		try {
			em.remove(em.merge(entity));
		}
		catch(PersistenceException e){
			throw new ReportQueryDAOException("Failed deleting Report query "+entity.getId(), e);
		}
	}
	/**
	 * as the find method will be used in transaction context, added required transaction attribute type
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public ReportQuery find(int id) throws ReportQueryDAOException {
		try {
			return em.find(ReportQuery.class, id);
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
}