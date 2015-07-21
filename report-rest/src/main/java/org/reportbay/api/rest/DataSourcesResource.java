package org.reportbay.api.rest;

import java.util.List;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.reportbay.api.dto.datasource.RestDataSources;
import org.reportbay.api.dto.datasource.RestTable;
import org.reportbay.api.dto.datasource.RestTables;
import org.reportbay.api.rest.exception.CustomizedWebException;
import org.reportbay.datasource.domain.Datasource;
import org.reportbay.datasource.service.DatasourceHandler;
import org.reportbay.datasource.service.JdbcClient;
import org.reportbay.datasource.service.exception.DatasourceHandlerException;
import org.reportbay.datasource.service.exception.JdbcClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Path("datasources")
public class DataSourcesResource{
	
	private final Logger LOG = LoggerFactory.getLogger(DataSourcesResource.class);
	
	@Inject
	private DatasourceHandler dataSourceService;
	
	@Inject
	private JdbcClient jdbcClient;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	 public RestDataSources getAllDataSources(){
		LOG.info("get all data sources");
		RestDataSources datasources = new RestDataSources();
		
		try {
			List<Datasource> datasourceList = dataSourceService.findAll();
			
			if(datasourceList!=null){
				datasources.getDatasources().addAll(datasourceList);

				// *** display password for edit data source UI ***
				//mask off password for security concern
				//maskDataSourcesPassword(datasourceList);
			}
		} catch (DatasourceHandlerException e) {
			LOG.warn("Exception in finding all datasources", e);
			throw new CustomizedWebException(Response.Status.NOT_FOUND, e.getMessage());
		}
		
		return datasources;
	}
	
	/**
	 * 
	 * @param datasource (id must be 0 during creation)
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Datasource createDataSource(Datasource datasource){
		LOG.info("create data sources");
		Datasource createdDatasource = null;
		
		if(datasource!=null && datasource.getId() == 0){
			LOG.warn("datasource id [{}] not 0 during creation ",datasource.getId());
			throw new CustomizedWebException(Response.Status.BAD_REQUEST,"datasource id ["+datasource.getId()+"] not 0 during creation ");
		}
		try {
			createdDatasource = dataSourceService.save(datasource);
		} 
		catch (Exception e ) {
			LOG.warn("Exception in creating datasource", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
		return createdDatasource;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Datasource updateDataSource(Datasource datasource){
		LOG.info("update data sources");
		try {
			if(datasource==null || (dataSourceService.find(datasource.getId()) == null)){
				String errMsg = "datasource to be updated not found";
				LOG.warn(errMsg); 
				throw new CustomizedWebException(Response.Status.BAD_REQUEST, errMsg);
			}
			
			dataSourceService.update(datasource);
		}
		catch (Exception e ) {
			LOG.warn("Exception in updating datasource", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
		return datasource;
	}
	
	@GET
	@Path("/{datasourceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Datasource getDataSource(@PathParam("datasourceId") int datasourceId){
		LOG.info("get data source");
		Datasource datasource = null;
    	
    	try {
    		datasource = dataSourceService.find(datasourceId);
    		
    		if(datasource==null){
    			LOG.warn("unable to find datasource for id {}",datasourceId);
    			throw new CustomizedWebException(Response.Status.NOT_FOUND, "datasource not found");
    		}
		} catch (Exception e) {
			LOG.warn("Exception in finding datasource", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
    	
    	return datasource;
	}
	
	
	@DELETE
    @Path("/{datasourceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDataSource(@PathParam("datasourceId") int datasourceId){
		LOG.info("delete data sources");
    	Datasource datasource = null;
    	Response.ResponseBuilder builder = null;
    	
		try {
			datasource = dataSourceService.find(datasourceId);
		} catch (Exception e) {
			builder = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.TEXT_PLAIN);
			LOG.warn("unable to find datasource for id {}",datasourceId);
		}
		
		if(datasource!=null){
			try {
				dataSourceService.delete(datasource);
				builder = Response.ok();
			} catch (Exception e) {
				//Failed to delete
				builder = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.TEXT_PLAIN);
				LOG.warn("Exception in deleting datasource", e);
			}
		}
		else{
			builder = Response.status(Status.NOT_FOUND);
			LOG.warn("unable to find datasource for id {}",datasourceId);
		}
		
		return builder.build();
    }
	
	@GET
	@Path("/{datasourceId}/tables")
	@Produces(MediaType.APPLICATION_JSON)
	public RestTables getDataSourceTable(@PathParam("datasourceId") int datasourceId){

		LOG.info("get table(s) of data sources");
		//create an envelope
		RestTables tables = new RestTables();
		try {
			//1. find the datasource 
    		Datasource datasource = dataSourceService.find(datasourceId);
    		
    		if(datasource==null){
    			LOG.warn("unable to find datasource for id {}",datasourceId);
    			throw new CustomizedWebException(Response.Status.NOT_FOUND, "datasource not found");
    		}
    		
    		//2. obtain the table(s) name by data source
    		List<String> tableNameList = jdbcClient.getTableNames(datasource);
    		
    		//3. construct table name for return
    		for(String tableName : tableNameList){
    			tables.getTables().add(new RestTable(tableName));
    		}
		} 
		catch (DatasourceHandlerException e) {
			LOG.warn("Exception in finding datasource", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		catch (JdbcClientException e) {
			LOG.warn("Exception in getting datasource's table name", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return tables;
	}
	
	/********** private method ******/
	/**
	 * 
	 * @param datasourceList
	 */
	@SuppressWarnings("unused")
	private void maskDataSourcesPassword(List<Datasource> datasourceList){
		
		for(Datasource dataSource: datasourceList){
			dataSource.setPassword("********");
		}
	}
}