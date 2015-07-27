package org.reportbay.api.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.reportbay.api.dto.schedule.Schedule;
import org.reportbay.api.dto.schedule.ScheduleTask;
import org.reportbay.api.dto.schedule.ScheduleTasks;
import org.reportbay.api.rest.exception.CustomizedWebException;
import org.reportbay.schedule.domain.ScheduleJob;
import org.reportbay.schedule.service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Path("schedules")
public class ScheduleResource{
	private final Logger LOG = LoggerFactory.getLogger(ScheduleResource.class);

    @Context
    ResourceContext rc;
    
    @Inject
    private SchedulerService schedulerService;
    
    /**
	 * 
	 * @return
	 */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ScheduleTasks getScheduleTasks(){
    	LOG.info("get schedule Tasks");
    	
    	ScheduleTasks scheduleTasks = new ScheduleTasks();
    	
    	try {
    		//TODO:
		} 
    	catch (Exception e) {
			LOG.error("Exception in finding schedule tasks", e);
			throw new CustomizedWebException(Response.Status.NOT_FOUND, e.getMessage());
		}
    	
    	return scheduleTasks;
    }

    /**
     * 
     * @param scheduleTask
     * @return
     */
    //use POST as it is not idempotent, each call with same content may lead to creation of a new record
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ScheduleTask createScheduleTask(ScheduleTask scheduleTask){
    	
    	LOG.info("create schedule task");
    	
    	ScheduleTask result = null;
    	
    	try{
    		//1. transform schedule task to scheduler POJO
    		ScheduleJob scheduler = mapScheduleTaskToScheduler(scheduleTask); 

    		//2. create scheduler entry
    		scheduler = schedulerService.save(scheduler);
    		
    		//3. transform schedule POJO to schedule task
    		result = mapSchedulerToScheduleTask(scheduler);
    	}
    	catch(Exception e) {
			LOG.error("Exception in creating schedule task", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
    	
    	return result;
    }

    /**
     * 
     * @param secheduleTask
     * @return
     */
    //use PUT as update is idempotent, always update same resource for same content (identify by id) 
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ScheduleTask updateScheduleTask(ScheduleTask secheduleTask){
    	LOG.info("update schedule task");
    	
    	ScheduleTask result = null;
    	
    	try{
    		//TODO:
    	}
    	catch(Exception e) {
			LOG.error("Exception in updating schedule task", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
    	
    	return result;
    }

    /**
     * 
     * @param scheduleTaskId
     * @return
     */
    @GET
    @Path("/{scheduleTaskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ScheduleTask getScheduleTask(@PathParam("scheduleTaskId") int scheduleTaskId){
    	LOG.info("find schedule task");
    	
    	ScheduleTask result = null;
    	
    	try{
    		//TODO:
    	}
    	catch(Exception e) {
			LOG.error("Exception in finding schedule task", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
    	
    	return result;
    }
    
    /**
     * 
     * @param scheduleTaskId
     * @return
     */
    @DELETE
    @Path("/{scheduleTaskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteScheduleTask(@PathParam("scheduleTaskId") int scheduleTaskId){
    	LOG.info("delete schedule task");
    	
    	Response.ResponseBuilder builder = null;
    	
    	try{
    		//TODO:
    		//2. success and prepare status ok
			builder = Response.ok();
		} 
		catch (Exception e) {
			builder = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.TEXT_PLAIN);
			LOG.error("failed to delete schedule task for {}",scheduleTaskId);
		}
		
		return builder.build();
    }
    
    /************ private methods *************/
    
    private ScheduleJob mapScheduleTaskToScheduler(ScheduleTask restTask){
    	ScheduleJob job = new ScheduleJob();
    	
    	job.setName(restTask.getName());
    	job.setTemplateId(restTask.getReportConnectorId());
    	job.setCallbackUrl(restTask.getCallbackUrl());
    	job.setSchedule(deriveSchedule(restTask.getSchedule()));
    	return job;
    }
    
    private String deriveSchedule(Schedule schedule){
    	//TODO:
    	return null;
    }
    
    private ScheduleTask mapSchedulerToScheduleTask(ScheduleJob scheduleJob){
    	
    	//TODO:
    	return null; 
    }
    
}