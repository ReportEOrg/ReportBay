package org.reporte.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.ReorderEvent;
import org.reporte.model.domain.AttributeMapping;
import org.reporte.model.domain.ColumnMetadata;
import org.reporte.model.domain.Datasource;
import org.reporte.model.domain.Model;
import org.reporte.model.domain.ModelQuery;
import org.reporte.model.domain.SimpleModel;
import org.reporte.model.domain.Model.Approach;
import org.reporte.model.service.ModelService;
import org.reporte.model.service.exception.DatasourceHandlerException;
import org.reporte.model.service.exception.JdbcClientException;
import org.reporte.model.service.exception.ModelServiceException;
import org.reporte.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model Wizard JSF backing bean 
 *
 */
@Named("modelWizard")
@ViewScoped
public class ModelWizardBean implements Serializable {
	private static final long serialVersionUID = 4607556957265028946L;
	private static final Logger LOG = LoggerFactory
			.getLogger(ModelWizardBean.class);

	private static final String SINGLE_TABLE = "Single Table";
	private static final String JOIN_QUERY = "Join Query";
	private static final String SELECT_QUERY = "SELECT * FROM %s";
	private static final int DEFAULT_QUERY_ROW_LIMIT = 5;

	private List<Datasource> datasources;
	private List<String> tableNames;
	private List<AttributeMapping> columnNames;
	private Model model;
	private boolean showSingleTablePanel = true;
	private boolean showJoinQueryPanel;
	private boolean disabledResultTab = true;
	private boolean disabledModelTab = true;
	private Set<ColumnMetadata> columns;
	private List<Map<ColumnMetadata, String>> resultSet;
	private List<Map<ColumnMetadata, String>> originalResultSet;
	private int activeIndex;
	private int limit = DEFAULT_QUERY_ROW_LIMIT;
	private int noOfRowsReturned;
	private String approach = "Single Table";
	private boolean requiredDatasource = true;
	private boolean requiredTargetTbl = true;
	private boolean disabledNextNav = false;

	// Parameters passed via Dialog Framework
	private int modelId;
	private String title;

	@Inject
	private ModelService modelService;

	@PostConstruct
	public void init() {
		// Retrieve the params passed via Dialog Framework.
		Map<String, String> requestParams = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		title = requestParams.get("title");
		modelId = requestParams.containsKey("id") ? Integer
				.valueOf(requestParams.get("id")) : 0;

		if (modelId != 0) {
			try {
				model = modelService.find(modelId);
			} catch (ModelServiceException e) {
				LOG.error(
						"Error loading model with given Id[" + modelId + "].",
						e);
			}

			try {
				tableNames = modelService.getJdbcClient().getTableNames(
						model.getDatasource());
			} catch (JdbcClientException e) {
				LOG.error(
						"Error loading available table names from target datasource["
								+ model.getDatasource().getName() + "].", e);
			}

			try {
				if (model.getApproach().equals(Approach.SINGLE_TABLE)) {
					columnNames = convertIntoAttributeMappings(modelService
							.getJdbcClient().getColumns(model.getDatasource(),
									((SimpleModel) model).getTable()));
				} else {
					columnNames = deriveColumnsFromQuery(model.getQuery()
							.getValue());
				}

				for (AttributeMapping attribute : model.getAttributeBindings()) {
					for (AttributeMapping column : columnNames) {
						if (attribute.getReferencedColumn().equals(
								column.getReferencedColumn())) {
							column.setAlias(attribute.getAlias());
							column.setId(attribute.getId());
							break;
						}
					}
				}

			} catch (JdbcClientException e) {
				LOG.error(
						"Error resolving columns for selected Model["
								+ model.getName() + "].", e);
			}
		} else {
			model = new SimpleModel();
			model.setQuery(new ModelQuery());
			model.setAttributeBindings(new ArrayList<AttributeMapping>());
		}

		try {
			datasources = modelService.getDatasourceHandler().findAll();
		} catch (DatasourceHandlerException e) {
			LOG.error("Failed to load existing datasources.", e);
		}
	}

	// ////////////////////////////////////////////////
	// GETTER & SETTER METHODS //
	// ////////////////////////////////////////////////

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public List<Datasource> getDatasources() {
		return datasources;
	}

	public void setDatasources(List<Datasource> datasources) {
		this.datasources = datasources;
	}

	public List<String> getTableNames() {
		return tableNames;
	}

	public void setTableNames(List<String> tableNames) {
		this.tableNames = tableNames;
	}

	public boolean isShowSingleTablePanel() {
		return showSingleTablePanel;
	}

	public void setShowSingleTablePanel(boolean showSingleTablePanel) {
		this.showSingleTablePanel = showSingleTablePanel;
	}

	public boolean isShowJoinQueryPanel() {
		return showJoinQueryPanel;
	}

	public void setShowJoinQueryPanel(boolean showJoinQueryPanel) {
		this.showJoinQueryPanel = showJoinQueryPanel;
	}

	public String getApproach() {
		return approach;
	}

	public void setApproach(String approach) {
		this.approach = approach;
	}

	public List<AttributeMapping> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<AttributeMapping> columnNames) {
		this.columnNames = columnNames;
	}

	public boolean isDisabledResultTab() {
		return disabledResultTab;
	}

	public void setDisabledResultTab(boolean disabledResultTab) {
		this.disabledResultTab = disabledResultTab;
	}

	public boolean isDisabledModelTab() {
		return disabledModelTab;
	}

	public void setDisabledModelTab(boolean disabledModelTab) {
		this.disabledModelTab = disabledModelTab;
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public List<Map<ColumnMetadata, String>> getResultSet() {
		return resultSet;
	}

	public void setResultSet(List<Map<ColumnMetadata, String>> resultSet) {
		this.resultSet = resultSet;
	}

	public Set<ColumnMetadata> getColumns() {
		return columns;
	}

	public void setColumns(Set<ColumnMetadata> columns) {
		this.columns = columns;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isRequiredDatasource() {
		return requiredDatasource;
	}

	public void setRequiredDatasource(boolean requiredDatasource) {
		this.requiredDatasource = requiredDatasource;
	}

	public boolean isRequiredTargetTbl() {
		return requiredTargetTbl;
	}

	public void setRequiredTargetTbl(boolean requiredTargetTbl) {
		this.requiredTargetTbl = requiredTargetTbl;
	}

	public int getNoOfRowsReturned() {
		return noOfRowsReturned;
	}

	public void setNoOfRowsReturned(int noOfRowsReturned) {
		this.noOfRowsReturned = noOfRowsReturned;
	}

	public boolean isDisabledNextNav() {
		return disabledNextNav;
	}

	public void setDisabledNextNav(boolean disabledNextNav) {
		this.disabledNextNav = disabledNextNav;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// ////////////////////////////////////////////////
	// PRIVATE METHODS //
	// ////////////////////////////////////////////////

	private List<AttributeMapping> convertIntoAttributeMappings(
			List<ColumnMetadata> colList) {
		List<AttributeMapping> list = new ArrayList<AttributeMapping>();

		if (CollectionUtils.isNotEmpty(colList)) {
			int i = 1;
			for (ColumnMetadata column : colList) {
				AttributeMapping mapping = new AttributeMapping();
				mapping.setReferencedColumn(column.getLabel());
				mapping.setAlias(column.getLabel());
				mapping.setTypeName(column.getTypeName());
				mapping.setOrder(i++);

				list.add(mapping);
			}
		}
		return list;
	}

	private List<Map<ColumnMetadata, String>> applyLimit(List<Map<ColumnMetadata, String>> resultSet, int limit) {
		List<Map<ColumnMetadata, String>> rs = new ArrayList<Map<ColumnMetadata, String>>();
		for (int i = 0; i < resultSet.size(); i++) {
			if (i == limit) {
				break;
			}
			rs.add(resultSet.get(i));
		}
		return rs;
	}

	// ////////////////////////////////////////////////
	// ACTION LISTENER METHODS //
	// ////////////////////////////////////////////////

	public void onApproachChange() {
		if (SINGLE_TABLE.equalsIgnoreCase(approach)) {
			showSingleTablePanel = true;
			showJoinQueryPanel = false;
			disabledNextNav = false;
			((SimpleModel) model).setTable(null);
		} else if (JOIN_QUERY.equalsIgnoreCase(approach)) {
			showJoinQueryPanel = true;
			showSingleTablePanel = false;
			disabledNextNav = true;
			activeIndex = 0;
			disabledResultTab = true;
			disabledModelTab = true;
			if (CollectionUtils.isNotEmpty(resultSet)) {
				resultSet.clear();
			}
		}
		model.getQuery().setValue(null);
		if (CollectionUtils.isNotEmpty(columnNames)) {
			columnNames.clear();
		}

		model.getAttributeBindings().clear();
	}

	public void onDatasourceSltOneMenuChange() {
		Datasource selectedDatasource = model.getDatasource();
		if (selectedDatasource != null) {
			try {
				tableNames = modelService.getJdbcClient().getTableNames(
						selectedDatasource);
			} catch (JdbcClientException e) {
				LOG.error(
						"Failed to load available table names for selected Datasource with name["
								+ selectedDatasource.getName() + "].", e);
				tableNames = new ArrayList<String>();
			}
			setRequiredDatasource(false);
		} else {
			tableNames = new ArrayList<String>();
			model.setDatasource(null);
			setRequiredDatasource(true);
		}
	}

	public void onLimitSltOneMenuChange() {
		resultSet = applyLimit(originalResultSet, limit);
	}

	public void onTableSltOneMenuChange() {
		String tableName = ((SimpleModel) model).getTable();
		if (StringUtils.isNotEmpty(tableName)) {
			try {
				List<ColumnMetadata> columns = modelService.getJdbcClient()
						.getColumns(model.getDatasource(), tableName);
				columnNames = convertIntoAttributeMappings(columns);
			} catch (JdbcClientException e) {
				LOG.error("Failed to load metadata columns for the table - "
						+ tableName + ".");
				columnNames = new ArrayList<AttributeMapping>();
			}
			model.getQuery().setValue(String.format(SELECT_QUERY, tableName));
			model.getAttributeBindings().clear();
			model.getAttributeBindings().addAll(columnNames);
			setRequiredTargetTbl(false);
		} else {
			columnNames = new ArrayList<AttributeMapping>();
			model.setAttributeBindings(columnNames);
			((SimpleModel) model).setTable(null);
			setRequiredTargetTbl(true);
		}
	}

	public void onChangeTxtAreaQuery() {
		disabledResultTab = true;
		disabledModelTab = true;
		activeIndex = 0;
		disabledNextNav = true;
	}

	public void onReorder(ReorderEvent event) {
		// During this event, the order on the UI reflect here. 
		// So, we better reset the order here.
		int i = 1;
		for (AttributeMapping mapping : model.getAttributeBindings()) {
			mapping.setOrder(i++);
		}
	}

	public String onFlowProcess(FlowEvent event) {
		model.getAttributeBindings();
		if (event.getNewStep().equals("confirmation")) {
			// Rearrange the mappings for display during summary according to
			// the order as they were defined by user.
			Collections.sort(model.getAttributeBindings(), new Comparator<AttributeMapping>() {

				@Override
				public int compare(AttributeMapping o1, AttributeMapping o2) {
					return o1.getOrder() - o2.getOrder();
				}
			});
		}
		return event.getNewStep();
	}

	// ////////////////////////////////////////////////
	// 				ACTION METHODS 					 //
	// ////////////////////////////////////////////////

	private String applyDefaultLimit(String query) {
		query = query.replace(";", "");
		// TODO: Regex is currently not working. Need to find out why.
		String regex = "/\\s+LIMIT\\s+\\d+$/i";
		if (query.matches(regex)) {
			query = StringUtils.replacePattern(query.trim(), regex, "");
		}

		return query.concat(" LIMIT 20");
	}

	private List<AttributeMapping> deriveColumnsFromQuery(String query)
			throws JdbcClientException {
		List<AttributeMapping> columnNames = new ArrayList<AttributeMapping>();

		String queryWithLimit = applyDefaultLimit(query);
		originalResultSet = modelService.getJdbcClient().execute(
				model.getDatasource(), queryWithLimit);
		//TODO: do we really need this?
		// noOfRowsReturned = originalResultSet.size();
		resultSet = applyLimit(originalResultSet, limit);

		if (CollectionUtils.isNotEmpty(resultSet)) {
			columns = resultSet.get(0).keySet();
			int i = 1;
			for (ColumnMetadata column : columns) {
				AttributeMapping mapping = new AttributeMapping();
				mapping.setReferencedColumn(column.getLabel());
				mapping.setTypeName(column.getTypeName());
				mapping.setAlias(column.getLabel());
				mapping.setOrder(i++);

				columnNames.add(mapping);
			}
		}
		return columnNames;
	}

	public void verify() {
		model.getAttributeBindings().clear();
		columnNames = new ArrayList<AttributeMapping>();
		columns = new HashSet<ColumnMetadata>();

		try {
			columnNames = deriveColumnsFromQuery(model.getQuery().getValue());
			model.getAttributeBindings().addAll(columnNames);

			// Enable and move to 'Result' tab.
			disabledResultTab = false;
			activeIndex = 1;

			WebUtils.addInfoMessage("Query verification was successful.");
		} catch (JdbcClientException e) {
			LOG.error("Query verification failed. Executed query = ["
					+ model.getQuery().getValue() + "].", e);
			WebUtils.addErrorMessage(e.getCause().getMessage());
		}
	}

	public void proceedToModel() {
		disabledModelTab = false;
		activeIndex = 2;
		disabledNextNav = false;
	}

	public void finish() {
		String action = null;
		String status = null;

		try {

			if (modelId == 0) {
				action = "create";
				modelService.save(model);
			} else {
				action = "update";
				modelService.update(model);
			}
			status = "success";
		} catch (ModelServiceException e) {
			LOG.error("Failed to " + action + " Model.", e);
			status = "failed";
		}
		// Prepare data to pass it back to whatever that opened this dialog.
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("action", action);
		data.put("payload", model.getName());
		data.put("status", status);

		RequestContext.getCurrentInstance().closeDialog(data);
	}

	public void cancel() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}
}
