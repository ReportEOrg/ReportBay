package org.reportbay.api.rest;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.reportbay.api.dto.datasource.RestDatabaseType;
import org.reportbay.api.rest.exception.CustomizedWebException;
import org.reportbay.datasource.domain.DatabaseType;
import org.reportbay.datasource.service.DatabaseTypeHandler;
import org.reportbay.datasource.service.exception.DatabaseTypeHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Path("databasetypes")
public class DatabaseTypesResource {
	
private final Logger LOG = LoggerFactory.getLogger(DatabaseTypesResource.class);
	
	@Inject
	private DatabaseTypeHandler databaseTypeService;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	 public RestDatabaseType getAllDatabaseTypes(){
		LOG.info("get all database types");
		RestDatabaseType databaseTypes = new RestDatabaseType();
		try {
			List<DatabaseType> databaseTypeList = databaseTypeService.findAll();
			if(databaseTypeList != null) {
				databaseTypes.getTypes().addAll(databaseTypeList);
			}
		} catch (DatabaseTypeHandlerException e) {
			LOG.warn("Exception in finding all database types", e);
			throw new CustomizedWebException(Response.Status.NOT_FOUND, e.getMessage());
		}
		return databaseTypes;
	}

}
