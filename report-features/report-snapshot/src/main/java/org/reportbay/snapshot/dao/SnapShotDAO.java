package org.reportbay.snapshot.dao;

import java.util.List;

import org.reportbay.common.dao.BaseDAO;
import org.reportbay.snapshot.dao.exception.SnapShotDAOException;
import org.reportbay.snapshot.domain.ReportSnapShot;
import org.reportbay.snapshot.domain.ReportSnapShotBase;


public interface SnapShotDAO extends BaseDAO<ReportSnapShot, SnapShotDAOException> {
	
	/**
	 * 
	 * @param entity
	 * @return
	 * @throws SnapShotDAOException
	 */
	ReportSnapShot updateEntity(ReportSnapShot entity) throws SnapShotDAOException;
	/**
	 * 
	 * @return
	 * @throws SnapShotDAOException
	 */
	List<ReportSnapShotBase> findAllBase() throws SnapShotDAOException;
}
