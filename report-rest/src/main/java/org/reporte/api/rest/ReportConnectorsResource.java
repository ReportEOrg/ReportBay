package org.reporte.api.rest;

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

import org.reporte.api.dto.reportconnector.RestReportConnector;
import org.reporte.api.dto.reportconnector.RestReportConnectors;
import org.reporte.api.rest.exception.CustomizedWebException;
import org.reporte.api.service.ReportConnectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
@Path("reportconnectors")
public class ReportConnectorsResource{
	private final Logger LOG = LoggerFactory.getLogger(ReportConnectorsResource.class);

    @Context
    ResourceContext rc;
    
	@Inject
	private ReportConnectorService reportConnectorService;
    
	/**
	 * 
	 * @return
	 */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RestReportConnectors getAllReportConnectors(){
    	
    	RestReportConnectors connectors = new RestReportConnectors();
    	
    	try {
    		connectors = reportConnectorService.findAllReportConnectors();
		} 
    	catch (Exception e) {
			LOG.error("Exception in finding all report connectors", e);
			throw new CustomizedWebException(Response.Status.NOT_FOUND, e.getMessage());
		}
    	
    	return connectors;
    }
    
    /**
     * 
     * @param connector
     * @return
     */
    //use POST as it is not idempotent, each call with same content may lead to creation of a new record
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestReportConnector createReportConnector(RestReportConnector connector){
    	
    	RestReportConnector resultConnector = null;

    	try {
    		resultConnector = reportConnectorService.save(connector);
		}
    	catch (Exception e) {
			LOG.error("Exception in creating report connector", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
    	
    	return resultConnector;
    }

    /**
     * 
     * @param connector
     * @return
     */
    //use PUT as update is idempotent, always update same resource for same content (identify by id) 
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestReportConnector updateReportConnector(RestReportConnector connector){
    	
    	RestReportConnector resultConnector = null;

    	try {
    		resultConnector = reportConnectorService.update(connector);
		}
    	catch (Exception e) {
			LOG.error("Exception in updating report connector", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
    	
    	return resultConnector;
    }

    /**
     * 
     * @param reportConnectorId
     * @return
     */
    @GET
    @Path("/{reportConnectorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public RestReportConnector getReportConnector(@PathParam("reportConnectorId") int reportConnectorId){
    	RestReportConnector restConnector = null;
    	
    	try {
    		restConnector = reportConnectorService.find(reportConnectorId);
    	}
    	catch (Exception e) {
			LOG.error("Exception in finding report template", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
    	
    	return restConnector;
    }
    
    /**
     * 
     * @param reportConnectorId
     * @return
     */
    @DELETE
    @Path("/{reportConnectorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReportConnector(@PathParam("reportConnectorId") int reportConnectorId){
    	
    	Response.ResponseBuilder builder = null;
    	
		try {
			//1. delete by entity id
			reportConnectorService.delete(reportConnectorId);
			//2. success and prepare status ok
			builder = Response.ok();
		} 
		catch (Exception e) {
			builder = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.TEXT_PLAIN);
			LOG.error("failed to delete report connector for "+reportConnectorId);
		}
		
		return builder.build();
    }
}
