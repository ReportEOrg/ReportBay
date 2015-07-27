package org.reportbay.snapshot.service;

import java.util.List;

import javax.ejb.Local;

import org.reportbay.snapshot.domain.ReportSnapShot;
import org.reportbay.snapshot.domain.ReportSnapShotBase;
import org.reportbay.snapshot.service.exception.SnapShotServiceException;

@Local
public interface SnapShotService{
	
	/**
	 * 
	 * @param reportSnapShot
	 * @return
	 * @throws SnapShotServiceException
	 */
	ReportSnapShot save(ReportSnapShot reportSnapShot) throws SnapShotServiceException;
	
	/**
	 * 
	 * @param reportSnapShot
	 * @return
	 * @throws SnapShotServiceException
	 */
	ReportSnapShot update(ReportSnapShot reportSnapShot) throws SnapShotServiceException;
	
	/**
	 * 
	 * @param reportSnapShotId
	 * @return
	 * @throws SnapShotServiceException
	 */
	ReportSnapShot findReportSnapShot(int reportSnapShotId) throws SnapShotServiceException;
	
	/**
	 * 
	 * @return
	 * @throws SnapShotServiceException
	 */
	List<ReportSnapShotBase> findAllReportSnapShotBase() throws SnapShotServiceException ;
	
	/**
	 * 
	 * @param reportSnapShot
	 * @throws SnapShotServiceException
	 */
	void delete(ReportSnapShot reportSnapShot) throws SnapShotServiceException;
}