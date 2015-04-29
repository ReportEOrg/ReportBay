package org.reportbay.api.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.reportbay.api.dto.report.RestReport;
import org.reportbay.api.dto.reportconnector.RestReportConnector;
import org.reportbay.api.rest.exception.CustomizedWebException;
import org.reportbay.api.service.ReportConnectorService;
import org.reportbay.api.service.exception.ReportConnectorServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Path("reportgen")
public class ReportGenResource{
	
	private final Logger LOG = LoggerFactory.getLogger(ReportGenResource.class);

    @Context
    ResourceContext rc;
    
    @Inject ReportConnectorService reportConnectorService;
    
    /**
     * 
     * @param reportConnectorId
     * @return
     */
    @GET
    @Path("/{reportConnectorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public RestReport genReportByID(@PathParam("reportConnectorId") int reportConnectorId){
    	LOG.info("generate report by report connector id");
    	RestReport report = null;
    	
    	try{
    		report = reportConnectorService.generateReportPreview(reportConnectorId);
    	}
    	catch(ReportConnectorServiceException e){
    		LOG.warn("Exception in generating report preview for {}",reportConnectorId, e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
    	}
    	
    	return report;
    }
    
    /**
     *     
     * @param restReportConnector
     * @return
     */
    @POST
    @Path("/generateReportPreview")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestReport genReportPreview(RestReportConnector restReportConnector){
    	LOG.info("generate report preview by connector");
    	RestReport report = null;

    	try {
    		report = reportConnectorService.generateReportPreview(restReportConnector);
		} 
    	catch(ReportConnectorServiceException e){
    		LOG.warn("Exception in generating report preview ", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
    	}
    	
    	return report;
    }
}