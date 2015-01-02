package org.reporte.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.reporte.model.domain.Model;
import org.reporte.model.domain.SimpleModel;
import org.reporte.model.service.ModelService;
import org.reporte.model.service.exception.ModelServiceException;
import org.reporte.web.util.WebUtils;

/**
 * Maintain Model JSF Backing bean 
 *
 */
@Named("maintainModel")
@ViewScoped
public class MaintainModelBean implements Serializable {
	private static final long serialVersionUID = 696889640817167336L;
	private static final Logger LOG = Logger.getLogger(MaintainModelBean.class);

	private TreeNode modelTreeRoot;
	private TreeNode selectedNode;

	private Model model;
	private String query;
	private boolean renderedName;
	private int modelId;
	//for simple model UI table name
	private String modelTableName;

	@Inject
	private ModelService modelService;

	@PostConstruct
	public void init() {

		Model refModel = initTreeNode(null,null);
		
		if(refModel !=null){
			model = refModel;
			
			if(model instanceof SimpleModel){
				modelTableName = ((SimpleModel)model).getTable();
			}
		}
	}

	public Model getModel() {
		return model;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public boolean isRenderedName() {
		return renderedName;
	}

	public void setRenderedName(boolean renderedName) {
		this.renderedName = renderedName;
	}

	/**
	 * initialize the tree node(s) for Left hand side panel representing model(s) under respective datasource(s)
	 */
	private Model initTreeNode(String refDatasourceName, String refModelName){
		
		Model refModel = null;
		//1. create the root node
		modelTreeRoot = new DefaultTreeNode("Root",null);
		
		try {
			Map<String, TreeNode> dataSourceNodeLookupMap = new HashMap<String, TreeNode>();
			String datasourceName;
			TreeNode datasourceNode;
			TreeNode modelNode;

			//TODO: source datasource w/o model displayed too?
			//1. obtain all model(s) order by datasource name, model name
			List<Model> allModels = modelService.findAllOrderByDatasourceName();
			
			//2. for each model
			for (Model model : allModels) {
				
				datasourceName = model.getDatasource().getName();

				//2.a obtain datasource node based on datasource name
				datasourceNode = dataSourceNodeLookupMap.get(datasourceName);
				
				//2.b not yet exist create one
				if(datasourceNode==null){
					datasourceNode = new DefaultTreeNode(datasourceName,modelTreeRoot);
					dataSourceNodeLookupMap.put(datasourceName,datasourceNode);
				}
				
				//2.c create and append model node under datasource node
				modelNode = new DefaultTreeNode("modelNode", model, datasourceNode);
				datasourceNode.getChildren().add(modelNode);
				
				//not yet identify reference
				if(refModel == null
				   &&
				  //i. if not specified used first model found as reference
				  ((refDatasourceName == null && refModelName ==null) 
				   || 
				   //ii. if specified and match, made model as reference
				   (refDatasourceName!=null && refDatasourceName.equals(datasourceName) &&
					refModelName!=null && refModelName.equals(model.getName())))
				  )
				{
					refModel = model;
					
					datasourceNode.setExpanded(true);
					modelNode.setSelected(true);
				}
			}
		} catch (ModelServiceException e) {
			LOG.error("Failed to load all existing Models.", e);
		}
		
		return refModel;
	}
	
	private void openModelWizardDialog(Map<String, String> params) {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("draggable", false);
		options.put("resizable", true);
		options.put("contentWidth", 900);
		options.put("contentHeight", 500);

		Map<String, List<String>> requestParams = new HashMap<String, List<String>>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			List<String> values = new ArrayList<String>();
			values.add(entry.getValue());
			requestParams.put(entry.getKey(), values);
		}
		RequestContext.getCurrentInstance().openDialog("model_wizard", options, requestParams);
	}

	public void addModel() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("title", "New Model");

		openModelWizardDialog(params);
	}

	public void updateModel(String id) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("title", "Update Model");
		params.put("id", id);

		openModelWizardDialog(params);
	}

	public void deleteModel() {
		String tableName= model.getName();
		try {
			modelService.delete(model);

			//rebuild the tree and try to use 1st model as reference 
			Model refModel = initTreeNode(null, null);
			
			refreshViewModelTableName(refModel);
			
			WebUtils.addInfoMessage("Model '%s' has been successfully deleted.", tableName);
		} catch (ModelServiceException e) {
			LOG.error("Failed to delete Model with given name '" + tableName + "'.", e);
			WebUtils.addErrorMessage("An error was encountered while deleting Model with given name '%s'.",
					tableName);
		}
	}

	public void onDialogReturn(SelectEvent event) {
		@SuppressWarnings("unchecked")
		Map<String, String> data = (Map<String, String>) event.getObject();
		if (data != null) {
			String status = (String) data.get("status");
			String action = (String) data.get("action");
			String completedAction = action.equals("create") ? "created" : "updated";
			String completingAction = action.equals("create") ? "creating" : "updating";

			if (status.equals("success")) {
				//obtain the datasource name and model name from dialog
				String modelName = (String) data.get("modelName");
				String datasourceName = (String)data.get("datasourceName");
				WebUtils.addInfoMessage("Model '%s' has been successfully %s.", modelName, completedAction);
				
				//rebuild the tree and try to obtain the model reference of the model worked in dialog 
				Model refModel = initTreeNode(datasourceName, modelName);
				
				refreshViewModelTableName(refModel);
			} else {
				WebUtils.addErrorMessage("An error was encountered while %s Model.", completingAction);
			}
		}
	}
	
	/**
	 * handle select node event on tree's node
	 * @param event
	 */
	public void onNodeSelect(NodeSelectEvent event){
		String nodeType = event.getTreeNode().getType();
		
		//handle only if selected is model node
		if("modelNode".equals(nodeType)){
			refreshViewModelTableName((Model)event.getTreeNode().getData());
		}
	}
	
	/**
	 * 
	 * @param refModel
	 */
	private void refreshViewModelTableName(Model refModel){
		
		//if present, make it as active model in view
		if(refModel!=null){
			model = refModel;

			if(model instanceof SimpleModel){
				modelTableName = ((SimpleModel)model).getTable();
			}
			//otherwise clear value, for join query (complexModel )
			else{
				modelTableName = "";
			}
		}
	}
	
	public TreeNode getModelTreeRoot() {
		return modelTreeRoot;
	}

	/**
	 * @return the selectedNode
	 */
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * @param selectedNode the selectedNode to set
	 */
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	/**
	 * @return the modelTableName
	 */
	public String getModelTableName() {
		return modelTableName;
	}

	/**
	 * @param modelTableName the modelTableName to set
	 */
	public void setModelTableName(String modelTableName) {
		this.modelTableName = modelTableName;
	}

}
