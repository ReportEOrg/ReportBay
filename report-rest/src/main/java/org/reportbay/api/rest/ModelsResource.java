package org.reportbay.api.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

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

import org.apache.commons.collections.CollectionUtils;
import org.reportbay.api.dto.model.RestModel;
import org.reportbay.api.dto.model.RestModelPreviewResult;
import org.reportbay.api.dto.model.RestModels;
import org.reportbay.api.rest.exception.CustomizedWebException;
import org.reportbay.datasource.domain.ColumnMetadata;
import org.reportbay.datasource.domain.Datasource;
import org.reportbay.datasource.service.DatasourceHandler;
import org.reportbay.datasource.service.JdbcClient;
import org.reportbay.model.domain.AttributeMapping;
import org.reportbay.model.domain.ComplexModel;
import org.reportbay.model.domain.Model;
import org.reportbay.model.domain.SimpleModel;
import org.reportbay.model.service.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
@Path("models")
public class ModelsResource{
	private final Logger LOG = LoggerFactory.getLogger(ModelsResource.class);
	
    @Context
    ResourceContext rc;
    
	@Inject
	private ModelService modelService;
	
	@Inject
	private JdbcClient jdbcClient;
	
	@Inject
	private DatasourceHandler dataSourceService;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RestModels getAllModels(){
    	
    	LOG.info("getAllModels");
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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestModel createModel(RestModel restModel){
    	LOG.info("create Model");
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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestModel updateModel(RestModel restModel){
    	LOG.info("update Model");
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
    @Produces(MediaType.APPLICATION_JSON)
    public RestModel getModel(@PathParam("modelId") int modelId){
    	LOG.info("get Model {}", modelId);

    	RestModel restModel = null;
    	
    	//1. find model by id
    	Model model = findModel(modelId);

		//2. converted to REST domain model
		restModel = createRestModelFromModel(model);

    	return restModel;
    }
    
    @DELETE
    @Path("/{modelId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteModel(@PathParam("modelId") int modelId){
    	LOG.info("delete Model");
    	Model model = null;
    	Response.ResponseBuilder builder = null;
    	
		try {
			model = modelService.find(modelId);
		} catch (Exception e) {
			builder = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.TEXT_PLAIN);
			LOG.warn("unable to find model for id {}",modelId);
			return builder.build();
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
			LOG.warn("unable to find model for id {}",modelId);
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
    @Path("/derivemodelattributes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestModel deriveModelAttributes(RestModel restModel){
    	LOG.info("derive Model Attributes");
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
    @Path("/preview")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestModelPreviewResult generateModelPreview(RestModel restModel,
    												   //default max 20 records if not specified
    												   @DefaultValue("20") @QueryParam("maxRow") int maxRow){
    
    	LOG.info("generate Model Preview");
    	RestModelPreviewResult result = new RestModelPreviewResult();
    	
    	try{
    		
    		Datasource modelDatasource = restModel.getDatasource();
    		
    		Datasource dbDatasource = dataSourceService.find(modelDatasource.getId());
    		String modelQuery = restModel.getQuery().getValue();
    		
    		//1. find out the result's column in case no result match
    		List<ColumnMetadata> resultColumnList = jdbcClient.getColumnsFromQuery(dbDatasource, modelQuery);
    		
    		updateRestModelPreviewResultColumnName(result, resultColumnList);
    		
    		//2. find out count of total matched records
    		int matchRecordCount = jdbcClient.findQueryCount(dbDatasource, modelQuery);
    		
    		LOG.info("match record(s) = {}",matchRecordCount);
    		
    		result.setMatchRecordCount(matchRecordCount);
    		
    		//3. query result with result limit set 
    		List<Map<ColumnMetadata, String>> dbResultList = jdbcClient.execute(dbDatasource, modelQuery, maxRow);
    		
    		updateRestModelPreviewResult(result, dbResultList);
    		
    	}
    	catch(Exception e){
    		LOG.warn("Exception in generateModelPreview", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
    	}
    	
    	return result;
    }
    
    @GET
    @Path("/{modelId}/previewdata")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Map<String, String>> previewModelData(@PathParam("modelId") int id,
    												   //default max 20 records if not specified
    													//Set maxRow=-1 to retrieve all data
    												   @DefaultValue("20") @QueryParam("maxRow") int maxRow){
    	List<Map<String, String>> modelData = new ArrayList<Map<String,String>>();
    	try{
    		LOG.info("Previewing Model Data");
        	LOG.info("Model Id ["+id+"] and Maxrow ["+maxRow+"]");
    		if (id<=0) {
				throw new CustomizedWebException(Status.BAD_REQUEST, "ModelId must be > 0");
			}
    		Model model = modelService.find(id);
    		if (model==null) {
				throw new CustomizedWebException(Status.INTERNAL_SERVER_ERROR, "No Model found for the given Id ["+id+"]");
			}
    		//Get the attribute Mapping for the particular model
    		List<AttributeMapping> attributes = model.getAttributeBindings();
    		//Below map will store each field reference name as key in the attribute mapping along with its alias name as Value
    		Map<String, String> fieldNames = new HashMap<String, String>();
    		//Fetch all the reference column names and alias name into map.
    		//reference column name as key and alias name as value
    		for (AttributeMapping attributeMapping : attributes) {
				fieldNames.put(attributeMapping.getReferencedColumn(), attributeMapping.getAlias());
			}
    		LOG.debug("Attribute Mappings "+fieldNames);
    		LOG.info("Number of fiedlName "+ fieldNames.size());
    		//We expect the datastore object to be not null
    		Datasource modelDatasource = model.getDatasource();
    		String modelQuery = model.getQuery().getValue();
    		LOG.info("Model Query ["+modelQuery.replace("", "")+"]");
    		//1. Fetch data for the query with max row set 
    		List<Map<ColumnMetadata, String>> dbResultList = jdbcClient.execute(modelDatasource, modelQuery, maxRow);
    		if (CollectionUtils.isNotEmpty(dbResultList)) {
    			//Store the colummetadata in a map for faster processing
    			Map<String, Integer> columnMeta = new LinkedHashMap<String, Integer>();
    			LOG.debug("columMeta values are ["+columnMeta+"]");
    			LOG.info("Iterating Collection for each row");
				for (Map<ColumnMetadata, String> map : dbResultList) {
					Map<String, String> row = new HashMap<String, String>();
					for (Map.Entry<String, String> field : fieldNames.entrySet()) {
						//Use stream api to find the particular search field in the map and return if found
						Optional<Entry<ColumnMetadata, String>> column = map.entrySet().stream()
						.filter(m -> m.getKey().getLabel().equalsIgnoreCase(field.getKey()))
						.findFirst();
						if (column.isPresent()) {
							LOG.debug("Column Name ["+field.getValue()+"] and Value is ["+column.get().getValue()+"]");
							row.put(field.getValue(), column.get().getValue());
						}else{
							LOG.warn("Field ["+field.getValue()+"] Not found");
							row.put(field.getValue(), "");
						}
					}
					modelData.add(row);
				}
			}else{
				LOG.warn("Results for above Model Query is Empty");
			}
    	}catch(CustomizedWebException e){
    		LOG.info("Exception while previewing Model Data..", e);
    		throw e;
    	}catch(Exception e){
    		LOG.warn("Exception while previewing Model Data..", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
    	}
    	return modelData;
    }
    
    @GET
    @Path("/{modelId}/uniquedatafield")
    @Produces(MediaType.APPLICATION_JSON)
    public RestModelPreviewResult getUniqueDataFieldValue(@PathParam("modelId") int modelId,
    													  @QueryParam("dataField") String aliasDataField){
    	
    	RestModelPreviewResult result = new RestModelPreviewResult();
    	
    	//1. retrive model
    	Model model = findModel(modelId);
    	
    	try {
    		//2. extract unique value for selected model field
    		result.getColumnValueList().addAll(modelService.getModelFieldUniqueValue(model, aliasDataField));
			
		} catch (Exception e) {
			LOG.warn("Exception in getDataFieldValue", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
    	
    	return result;
    }
    
    /*************** private methods ****************/
    
    private Model findModel(int modelId){
    	Model model = null;
    	
    	try{
    		model = modelService.find(modelId);
    	}
    	catch(Exception e){
    		LOG.warn("Exception in finding model", e);
			throw new CustomizedWebException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
    	}
    	
    	if(model==null){
			LOG.warn("unable to find model for id {}",modelId);
			throw new CustomizedWebException(Response.Status.NOT_FOUND, "model not found");
		}
    	
    	return model;
    }
    
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
    	//mask off password of datasource befor returning via REST
    	maskDataSourcePassword(model.getDatasource());

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
	/**
	 * 
	 * @param datasource
	 */
	private void maskDataSourcePassword(Datasource dataSource){
		dataSource.setPassword("********");
	}
}