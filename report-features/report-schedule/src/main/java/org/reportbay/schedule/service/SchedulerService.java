package org.reportbay.schedule.service;

import java.util.List;

import javax.ejb.Local;

import org.reportbay.schedule.domain.ScheduleJob;
import org.reportbay.schedule.service.exception.ScheduleServiceException;

@Local
public interface SchedulerService{
	
	/**
	 * 
	 * @param scheduleJob
	 * @return
	 * @throws ScheduleServiceException
	 */
	ScheduleJob save(ScheduleJob scheduleJob) throws ScheduleServiceException;
	
	/**
	 * 
	 * @param scheduleJob
	 * @return
	 * @throws ScheduleServiceException
	 */
	ScheduleJob update(ScheduleJob scheduleJob) throws ScheduleServiceException;
	
	/**
	 * 
	 * @param scheduleJobId
	 * @return
	 * @throws ScheduleServiceException
	 */
	ScheduleJob findScheduleJob(int scheduleJobId) throws ScheduleServiceException;
	
	/**
	 * 
	 * @return
	 * @throws SnapShotServiceException
	 */
	List<ScheduleJob> findAllScheduleJob() throws ScheduleServiceException ;
	
	/**
	 * 
	 * @param scheduleJob
	 * @throws ScheduleServiceException
	 */
	void delete(ScheduleJob scheduleJob) throws ScheduleServiceException;
}