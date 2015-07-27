package org.reportbay.schedule.service.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.reportbay.schedule.dao.ScheduleJobDAO;
import org.reportbay.schedule.domain.ScheduleJob;
import org.reportbay.schedule.service.SchedulerService;
import org.reportbay.schedule.service.exception.ScheduleServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//stateless session bean
@Stateless
//container managed transaction manager
@TransactionManagement(TransactionManagementType.CONTAINER)
public class SchedulerServiceImpl implements SchedulerService{
	
	private final Logger LOG = LoggerFactory.getLogger(SchedulerServiceImpl.class);
	
	@Inject
	private ScheduleJobDAO scheduleJobDAO;

	@Override
	public ScheduleJob save(ScheduleJob scheduleJob)
			throws ScheduleServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScheduleJob update(ScheduleJob scheduleJob)
			throws ScheduleServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScheduleJob findScheduleJob(int scheduleJobId)
			throws ScheduleServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ScheduleJob> findAllScheduleJob()
			throws ScheduleServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(ScheduleJob scheduleJob) throws ScheduleServiceException {
		// TODO Auto-generated method stub
		
	}
	
}