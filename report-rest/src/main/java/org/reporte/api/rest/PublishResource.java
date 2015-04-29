package org.reporte.api.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.reportbay.publish.service.PublishService;
import org.reportbay.publish.service.exception.PublishServiceException;
import org.reporte.api.dto.publish.Publish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Path("publish")
public class PublishResource{
	
	private final Logger LOG = LoggerFactory.getLogger(PublishResource.class);
	
	private static final String PUBLISH_ERR_MSG = " %s option publish error : %s \n";

    @Context
    private ResourceContext rc;
    
    @Inject
    private PublishService dropBoxPublishService;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response publish(Publish publish) {
    	LOG.info("publishing ");
    	
    	Response.ResponseBuilder builder = null;
    	
    	try{
    		//1. prepare list of publish services and param
    		Map<PublishService,Properties> userPublishServices = obtainPublishServices(publish);
    		
    		StringBuilder errMessageSb = new StringBuilder(); 
    		
    		//2. process publish service(s) specified
    		for (Map.Entry<PublishService,Properties> publishServiceEntry : userPublishServices.entrySet()) {
    			
    			PublishService publishService = publishServiceEntry.getKey();
    			Properties params = publishServiceEntry.getValue();
    			
    			//perform publishing the report
    			try {
    				publishService.publish(publish.getPublishName(),params);
    			}
    			catch (PublishServiceException e) {
    				errMessageSb.append(String.format(PUBLISH_ERR_MSG, publish.getOption(), e.getMessage()));
    			}
    		}
    		
    		//if contain error
    		if(errMessageSb.length()>0){
    			builder = Response.status(Status.INTERNAL_SERVER_ERROR).entity(errMessageSb.toString()).type(MediaType.TEXT_PLAIN);
    		}
    		else{
    			builder = Response.ok();
    		}
    	}
    	catch(Exception e) {
			builder = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.TEXT_PLAIN);
			LOG.error("failed to publish ",e);
		}

    	return builder.build();
    }
    
    /**
     * 
     * @param publish
     * @return
     */
    private Map<PublishService,Properties> obtainPublishServices(Publish publish){
    	
    	Map<PublishService,Properties> publishServiceMap = new HashMap<PublishService,Properties>();
    	
    	//TODO: obtain session profile
    	Object profile = null;
    	
    	Properties prop = publish.getParams();

    	if(prop!=null){
        	//1. obtain supported publish service by session profile
        	Map<String, PublishService> supportedServices = obtainProfileSupportedPublishService(profile);

			PublishService publishService = supportedServices.get(publish.getOption());

			//2. if supported by profile
			if(publishService!=null){
				//prepare the service required param from profile
				publishService.prepareParamFromProfile(prop, profile);
				//added to pending process map
				publishServiceMap.put(publishService, prop);
			}
			
			//release for gc
			supportedServices.clear();
		}
    	
    	return publishServiceMap;
    }
	
    /**
     * @param profile //TODO: replace with proper class
     * @return
     */
    private Map<String, PublishService> obtainProfileSupportedPublishService(Object profile){
    	Map<String, PublishService> supportedServices = new HashMap<String, PublishService>();
    	
    	//TODO: obtain supported publish service by session profile
    	supportedServices.put(dropBoxPublishService.getServiceName(), dropBoxPublishService);
    	
    	return supportedServices;
    }
}