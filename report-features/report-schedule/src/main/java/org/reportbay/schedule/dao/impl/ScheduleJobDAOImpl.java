package org.reportbay.schedule.dao.impl;

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
import org.reportbay.schedule.dao.ScheduleJobDAO;
import org.reportbay.schedule.dao.exception.ScheduleJobDAOException;
import org.reportbay.schedule.domain.ScheduleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Local(ScheduleJobDAO.class)
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@Interceptors(DAOLogger.class)
public class ScheduleJobDAOImpl extends BaseDAOImpl<ScheduleJob, ScheduleJobDAOException> 
	implements ScheduleJobDAO, LogInterceptable<ScheduleJob>{
	
	private final Logger LOG = LoggerFactory.getLogger(ScheduleJobDAOImpl.class);
	
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
	public ScheduleJobDAOException createException(String msg, Throwable e) {
		return new ScheduleJobDAOException(msg, e);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<ScheduleJob> getEntityClass() {
		return ScheduleJob.class;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFindAllNamedQuery() {
		return "ReportSnapShot.findAll";
	}
}