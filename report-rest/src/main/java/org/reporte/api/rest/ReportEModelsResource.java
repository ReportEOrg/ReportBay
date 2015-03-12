package org.reporte.api.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.reporte.api.dto.model.RestModel;
import org.reporte.api.dto.model.RestModelPreviewResult;
import org.reporte.api.dto.model.RestModels;
import org.reporte.api.rest.exception.CustomizedWebException;
import org.reporte.datasource.domain.ColumnMetadata;
import org.reporte.datasource.domain.Datasource;
import org.reporte.datasource.service.JdbcClient;
import org.reporte.model.domain.ComplexModel;
import org.reporte.model.domain.Model;
import org.reporte.model.domain.SimpleModel;
import org.reporte.model.service.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
@Path("models")
public class ReportEModelsResource{
	private final Logger LOG = LoggerFactory.getLogger(ReportEModelsResource.class);

    @Context
    ResourceContext rc;
    
	@Inject
	private ModelService modelService;
	
	@Inject
	private JdbcClient jdbcClient;
    
    @GET
    @Produces("application/json")
    public RestModels getAllModels(){
    	
    	RestModels models = new RestModels();
    	
    	try {
    		List<Model> allModel = modelService.findAll();
			
			if(allModel!=null){
				for(Model model: allModel){
					updateMappedRestModels(models, model);
				}
			}
		} 
    	catch (Exception e) {
			LOG.warn("Exception in finding all model", e);
			throw new CustomizedWebException(Response.Status.NOT_FOUND, e.getMessage());
		}
    	
    	return models;
    }
    
    //use POST as it is not idempotent, each call with same content may lead to creation of a new record
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public RestModel createModel(RestModel restModel){
    	
    	RestModel resultModel = null;

    	try {
    		//1. convert REST model into domain model
    		Model model = convertRestModel(restModel);
    		
    		//2. save domain model
    		Model dbModel = modelService.save(model);
    		
    		//3. convert back result in REST model form
    		if(dbModel!=null){
    			resultModel = new RestModel(dbModel);
    			
    			if(dbModel instanceof SimpleModel){
    				resultModel.setTable(((SimpleModel) dbModel).getTable());
    			}
    		}
		} 
    	catch (Exception e) {
			LOG.warn("Exception in creating model", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
    	
    	return resultModel;
    }

    //use PUT as update is idempotent, always update same resource for same content (identify by id) 
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    public RestModel updateModel(RestModel restModel){
    	
    	RestModel resultModel = null;

    	try {
    		//1. convert REST model into domain model
    		Model model = convertRestModel(restModel);

    		//2. update domain model
    		modelService.update(model);
			
    		//3. convert back the result to REST domain model
			resultModel = createRestModelFromModel(model);
			
		} catch (Exception e) {
			LOG.warn("Exception in updating model", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
    	
    	return resultModel;
    }

    @GET
    @Path("/{modelId}")
    @Produces("application/json")
    public RestModel getModel(@PathParam("modelId") int modelId){
    	RestModel restModel = null;
    	
    	try {
    		//1. retrieve domain model by id
    		Model model = modelService.find(modelId);
    		
    		if(model==null){
    			LOG.warn("unable to find model for id "+modelId);
    			throw new CustomizedWebException(Response.Status.NOT_FOUND, "model not found");
    		}
    		//2. converted to REST domain model
    		restModel = createRestModelFromModel(model);
		} catch (Exception e) {
			LOG.warn("Exception in finding model", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
    	
    	return restModel;
    }
    
    @DELETE
    @Path("/{modelId}")
    @Produces("application/json")
    public Response deleteModel(@PathParam("modelId") int modelId){
    	
    	Model model = null;
    	Response.ResponseBuilder builder = null;
    	
		try {
			model = modelService.find(modelId);
		} catch (Exception e) {
			builder = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.TEXT_PLAIN);
			LOG.warn("unable to find model for id "+modelId);
		}
		
		if(model!=null){
			try {
				modelService.delete(model);
				builder = Response.ok();
			} catch (Exception e) {
				//Failed to delete
				builder = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.TEXT_PLAIN);
				LOG.warn("Exception in deleting model", e);
			}
		}
		else{
			builder = Response.status(Status.NOT_FOUND);
			LOG.warn("unable to find model for id "+modelId);
		}
		
		return builder.build();
    }
    
    /**
     * base on the model, derive the actual query + rebuild the attribute mapping
     * Use PUT as derive based on model is idempotent, always return same result for same content  
     * @param restModel
     * @return
     */
    @PUT
    @Path("/deriveModelAttributes")
    @Produces("application/json")
    @Consumes("application/json")
    public RestModel deriveModelAttributes(RestModel restModel){
    	
    	RestModel resultModel = null;
    	
    	try {
	    	Model model = convertRestModel(restModel);
	    	
	    	if(model instanceof ComplexModel){
	    		modelService.updateModelQueryFromJoinQuery(model);
	    	}
	    	else if (model instanceof SimpleModel){
	    		modelService.updateModelQueryFromSimpleQuery(model);
	    	}
	    	
	    	resultModel = createRestModelFromModel(model);
    	}
    	catch(Exception e){
    		LOG.warn("Exception in deriveModelAttributes", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
    	}
    	
    	return resultModel;
    }
    
    @POST
    @Path("/generatePreview")
    @Produces("application/json")
    @Consumes("application/json")
    public RestModelPreviewResult generateModelPreview(RestModel restModel,
    												   //default max 20 records if not specified
    												   @DefaultValue("20") @QueryParam("maxRow") int maxRow){
    
    	RestModelPreviewResult result = new RestModelPreviewResult();
    	
    	try{
    		
    		Datasource modelDatasource = restModel.getDatasource();
    		String modelQuery = restModel.getQuery().getValue();
    		
    		//1. find out the result's column in case no result match
    		List<ColumnMetadata> resultColumnList = jdbcClient.getColumnsFromQuery(modelDatasource, modelQuery);
    		
    		updateRestModelPreviewResultColumnName(result, resultColumnList);
    		
    		//2. find out count of total matched records
    		int matchRecordCount = jdbcClient.findQueryCount(modelDatasource, modelQuery);
    		
    		LOG.info("match record(s) = "+matchRecordCount);
    		
    		result.setMatchRecordCount(matchRecordCount);
    		
    		//3. query result with result limit set 
    		List<Map<ColumnMetadata, String>> dbResultList = jdbcClient.execute(modelDatasource, modelQuery, maxRow);
    		
    		updateRestModelPreviewResult(result, dbResultList);
    		
    		
    	}
    	catch(Exception e){
    		LOG.warn("Exception in generateModelPreview", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
    	}
    	
    	return result;
    }
    
    
    /*************** private methods ****************/
    
    /**
     * 
     * @param restModels
     * @param model
     */
    private void updateMappedRestModels(RestModels restModels, Model model){
    	
    	if(model !=null){
    		RestModel restModel = createRestModelFromModel(model);
    		
    		restModels.getModels().add(restModel);
    	}
    }
    /**
     * 
     * @param restModel
     * @return
     */
    private Model convertRestModel(RestModel restModel){
    	Model domainModel =null;
    	
    	if(Model.Approach.JOIN_QUERY.equals(restModel.getApproach())){
    		domainModel = new ComplexModel();
    		
    		mapRestModelToModel(restModel, domainModel);
    	}
    	else if(Model.Approach.SINGLE_TABLE.equals(restModel.getApproach())){
    		domainModel = new SimpleModel();
    		
    		mapRestModelToModel(restModel, domainModel);
    		((SimpleModel)domainModel).setTable(restModel.getTable());
    	}
    	
    	return domainModel;
    }
    
    /**
     * 
     * @param restModel
     * @param model
     */
    private void mapRestModelToModel(RestModel restModel, Model model){
    	model.setId(restModel.getId());
		model.setName(restModel.getName());
		model.setDescription(restModel.getDescription());
		model.setDatasource(restModel.getDatasource());
		model.setAttributeBindings(restModel.getAttributeBindings());
		model.setQuery(restModel.getQuery());
    }
    /**
     * 
     * @param model
     * @return
     */
    private RestModel createRestModelFromModel(Model model){
    	RestModel restModel = new RestModel(model);
    	
    	if(model instanceof SimpleModel){
			restModel.setTable(((SimpleModel) model).getTable());
		}
    	return restModel;
    }
    
    /**
     * 
     * @param previewResult
     * @param dbColumnNameList
     */
    private void updateRestModelPreviewResultColumnName(RestModelPreviewResult previewResult,
    												    List<ColumnMetadata> dbColumnNameList){
    	for(ColumnMetadata metadata: dbColumnNameList){
    		if(metadata!=null){
    			previewResult.getColumnNameList().add(metadata.getLabel());
    		}
    	}
    }
    
    /**
     * convert the db result into REST simplified format
     * @param previewResult
     * @param dbResultList
     */
    private void updateRestModelPreviewResult(RestModelPreviewResult previewResult, 
    										  List<Map<ColumnMetadata, String>> dbResultList){
    	
    	Map<String, String>restModelResultRowMap;
    	
    	for(Map<ColumnMetadata, String> dbRowFieldResultMap: dbResultList){
    		
    		if(dbRowFieldResultMap!=null){
    			restModelResultRowMap = new HashMap<String, String>();
    			previewResult.getResultRowList().add(restModelResultRowMap);
    			
    			//only take display label as key, result value as value
    			for(Map.Entry<ColumnMetadata, String> fieldResult: dbRowFieldResultMap.entrySet()){
    				ColumnMetadata fieldMetaData = fieldResult.getKey();
    				
    				restModelResultRowMap.put(fieldMetaData.getLabel(),fieldResult.getValue());
    			}
    		}
    	}
    }
    
}