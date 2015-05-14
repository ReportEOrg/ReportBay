package org.reportbay.snapshot.dao.impl;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.reportbay.common.dao.impl.BaseDAOImpl;
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
public class SnapShotDAOImpl extends BaseDAOImpl<ReportSnapShot, SnapShotDAOException> 
	implements SnapShotDAO, LogInterceptable<ReportSnapShot>{
	
	private final Logger LOG = LoggerFactory.getLogger(SnapShotDAOImpl.class);
	
	@PersistenceContext(unitName="reportbay")
	private EntityManager em;

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
	public SnapShotDAOException createException(String msg, Throwable e) {
		return new SnapShotDAOException(msg, e);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<ReportSnapShot> getEntityClass() {
		return ReportSnapShot.class;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFindAllNamedQuery() {
		return "ReportSnapShot.findAll";
	}
}