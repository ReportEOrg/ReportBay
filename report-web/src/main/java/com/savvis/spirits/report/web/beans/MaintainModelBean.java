package com.savvis.spirits.report.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import com.savvis.spirits.report.model.domain.Model;
import com.savvis.spirits.report.model.services.ModelService;
import com.savvis.spirits.report.model.services.ModelServiceException;
import com.savvis.spirits.report.web.util.WebUtils;

@Named("myBean")
@ViewScoped
public class MaintainModelBean implements Serializable {
	private static final long serialVersionUID = 696889640817167336L;
	private static final Logger LOG = Logger.getLogger(MaintainModelBean.class);

	private MenuModel menuModel;
	private Model model;
	private String query;
	private boolean renderedName;
	private int modelId;

	@Inject
	private ModelService modelService;

	@PostConstruct
	public void init() {
		menuModel = prepareLHSMenu();
		
		try {
			model = modelService.find(modelId);
		} catch (ModelServiceException e) {
			LOG.error("Failed to load Model with given Id [" + modelId + "].", e);
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

	public MenuModel getMenuModel() {
		return menuModel;
	}

	public void setMenuModel(MenuModel menuModel) {
		this.menuModel = menuModel;
	}

	public boolean isRenderedName() {
		return renderedName;
	}

	public void setRenderedName(boolean renderedName) {
		this.renderedName = renderedName;
	}

	private Map<String, List<String>> prepareModelsPerDatasource(List<Model> models) {
		// Used TreeMap to have keys in sorted order by their natural order.
		Map<String, List<String>> map = new TreeMap<String, List<String>>();
		for (Model model : models) {
			String datasourceName = model.getDatasource().getName();
			if (map.containsKey(datasourceName)) {
				List<String> modelNames = map.get(datasourceName);
				// add if not already included.
				if (!modelNames.contains(model.getName())) {
					modelNames.add(model.getName());
				}
			} else {
				List<String> modelNames = new ArrayList<String>();
				modelNames.add(model.getName());
				map.put(datasourceName, modelNames);
			}
		}
		return map;
	}

	private MenuModel prepareLHSMenu() {
		MenuModel menuModel = new DefaultMenuModel();

		try {
			List<Model> allModels = modelService.findAll();
			Map<String, List<String>> modelsPerDatasource = prepareModelsPerDatasource(allModels);
			for (Map.Entry<String, List<String>> entry : modelsPerDatasource.entrySet()) {
				List<String> modelNames = entry.getValue();
				// Sort the list in natural order.
				Collections.sort(modelNames);

				DefaultSubMenu subMenu = new DefaultSubMenu(entry.getKey());
				for (String modelName : modelNames) {
					DefaultMenuItem menuItem = new DefaultMenuItem(modelName);
					menuItem.setIcon("ui-icon-disk");
					subMenu.addElement(menuItem);
				}
				menuModel.addElement(subMenu);
			}
		} catch (ModelServiceException e) {
			LOG.error("Failed to load all existing Models.", e);
		}

		return menuModel;
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

	public void deleteModel(String id) {
		try {
			modelService.delete(model);
			WebUtils.addInfoMessage("Model '%s' has been successfully deleted.", model.getName());
		} catch (ModelServiceException e) {
			LOG.error("Failed to delete Model with given name '" + model.getName() + "'.", e);
			WebUtils.addErrorMessage("An error was encountered while deleting Model with given name '%s'.",
					model.getName());
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
				String modelName = (String) data.get("payload");
				WebUtils.addInfoMessage("Model '%s' has been successfully %s.", modelName, completedAction);
			} else {
				WebUtils.addErrorMessage("An error was encountered while %s Model.", completingAction);
			}
		}
	}
}
