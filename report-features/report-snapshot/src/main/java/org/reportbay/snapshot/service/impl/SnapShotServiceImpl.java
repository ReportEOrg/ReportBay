package org.reportbay.snapshot.service.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.reportbay.snapshot.dao.SnapShotDAO;
import org.reportbay.snapshot.dao.exception.SnapShotDAOException;
import org.reportbay.snapshot.domain.ReportSnapShot;
import org.reportbay.snapshot.domain.ReportSnapShotBase;
import org.reportbay.snapshot.service.SnapShotService;
import org.reportbay.snapshot.service.exception.SnapShotServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//stateless session bean
@Stateless
//container managed transaction manager
@TransactionManagement(TransactionManagementType.CONTAINER)
public class SnapShotServiceImpl implements SnapShotService{
	
	private final Logger LOG = LoggerFactory.getLogger(SnapShotServiceImpl.class);
	
	@Inject
	private SnapShotDAO snapShotDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ReportSnapShot save(ReportSnapShot reportSnapShot) throws SnapShotServiceException {

		LOG.debug("save");
		
		try{
			return snapShotDAO.insert(reportSnapShot);
		}
		catch(SnapShotDAOException ssde){
			throw new SnapShotServiceException(ssde);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ReportSnapShot update(ReportSnapShot reportSnapShot) throws SnapShotServiceException {
		LOG.debug("update");
		
		try{
			return snapShotDAO.updateEntity(reportSnapShot);
		}
		catch(SnapShotDAOException ssde){
			throw new SnapShotServiceException(ssde);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReportSnapShot findReportSnapShot(int reportSnapShotId) throws SnapShotServiceException {
		try{
			return snapShotDAO.find(reportSnapShotId);
		}
		catch(SnapShotDAOException ssde){
			throw new SnapShotServiceException(ssde);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ReportSnapShotBase> findAllReportSnapShotBase() throws SnapShotServiceException {
		try{
			return snapShotDAO.findAllBase();
		}
		catch(SnapShotDAOException ssde){
			throw new SnapShotServiceException(ssde);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(ReportSnapShot reportSnapShot) throws SnapShotServiceException {
		try{
			snapShotDAO.delete(reportSnapShot);
		}
		catch(SnapShotDAOException ssde){
			throw new SnapShotServiceException(ssde);
		}
	}
}