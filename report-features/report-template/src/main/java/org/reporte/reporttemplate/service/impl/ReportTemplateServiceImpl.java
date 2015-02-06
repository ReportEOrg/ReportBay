package org.reporte.reporttemplate.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.reporte.model.dao.ModelDAO;
import org.reporte.model.dao.exception.ModelDAOException;
import org.reporte.model.domain.AttributeMapping;
import org.reporte.model.domain.Model;
import org.reporte.model.domain.Model.Approach;
import org.reporte.model.domain.ModelQuery;
import org.reporte.model.domain.SimpleModel;
import org.reporte.reporttemplate.dao.ReportQueryDAO;
import org.reporte.reporttemplate.dao.ReportTemplateDAO;
import org.reporte.reporttemplate.dao.exception.ReportQueryDAOException;
import org.reporte.reporttemplate.dao.exception.ReportTemplateDAOException;
import org.reporte.reporttemplate.domain.AreaChartTemplate;
import org.reporte.reporttemplate.domain.BarChartTemplate;
import org.reporte.reporttemplate.domain.BaseReportTemplate;
import org.reporte.reporttemplate.domain.CartesianChartTemplate;
import org.reporte.reporttemplate.domain.ColumnChartTemplate;
import org.reporte.reporttemplate.domain.LineChartTemplate;
import org.reporte.reporttemplate.domain.PieChartTemplate;
import org.reporte.reporttemplate.domain.ReportQuery;
import org.reporte.reporttemplate.service.ReportTemplateService;
import org.reporte.reporttemplate.service.exception.ReportTemplateServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//stateless session bean
@Stateless
// container managed transaction manager
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ReportTemplateServiceImpl implements ReportTemplateService {

	private final Logger LOG = LoggerFactory.getLogger(ReportTemplateServiceImpl.class);

	private static final String SELECT_KEY = "select ";
	private static final String QUERY_SEPERATOR = ",";
	private static final String AS_KEY = " as ";
	private static final String APOSTROPHE_KEY = "'";
	private static final String DISTINCT="distinct ";

	@Inject
	private ReportTemplateDAO reportTemplateDAO;

	@Inject
	private ReportQueryDAO reportQueryDAO;

	@Inject
	private ModelDAO modelDAO;

	
	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private BaseReportTemplate saveReportTemplate(BaseReportTemplate reportTemplate) throws ReportTemplateServiceException {

		BaseReportTemplate managedReportTemplate = null;
		try {
			ReportQuery reportQuery = reportTemplate.getReportQuery();

			managedReportTemplate = reportTemplateDAO.insert(reportTemplate);

			// if pre generate query exist, create an entry
			if (reportQuery != null) {
				reportQuery.setId(managedReportTemplate.getId());

				reportQueryDAO.insert(reportQuery);
			}
		} catch (ReportQueryDAOException rqtde) {
			throw new ReportTemplateServiceException("Failed saving report query for " + reportTemplate.getTemplateName(), rqtde);
		} catch (ReportTemplateDAOException rtde) {
			throw new ReportTemplateServiceException("Failed saving report template for " + reportTemplate.getTemplateName(), rtde);
		}

		return managedReportTemplate;
	}

	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private BaseReportTemplate updateReportTemplateWithEntityCacheSync(BaseReportTemplate reportTemplate) throws ReportTemplateServiceException {

		BaseReportTemplate managedReportTemplate = null;

		try {
			// as JPA merge will "copy" the state of detached object into the
			// persistent entity and return the persistence entity reference,
			// hibernate entity manager appear to unaware of the "detached"
			// entity changes, if the same detached object is used subsequently,
			// hibernate entity manager may incorrectly assume the cached
			// "managed" entity still up to date and cause error at db level
			// operation
			// e.g. "Detached" entity having its OneToMany attribute removed and
			// merge(), followed by delete(), hibernate EM not aware that
			// the oneToMany attributes changes and attempt to perform remove
			// operation on entity no longer exist

			ReportQuery reportQuery = reportTemplate.getReportQuery();
			managedReportTemplate = reportTemplateDAO.updateEntity(reportTemplate);

			if (reportQuery != null) {

				ReportQuery managedReportQuery = reportQueryDAO.find(managedReportTemplate.getId());

				if (managedReportQuery == null) {
					reportQuery.setId(managedReportTemplate.getId());
					reportQueryDAO.insert(reportQuery);
				} else {
					managedReportQuery.setQuery(reportQuery.getQuery());
					managedReportQuery.setDatasource(reportQuery.getDatasource());
				}
			}
		} catch (ReportQueryDAOException rqde) {
			throw new ReportTemplateServiceException("Failed to update report query for " + reportTemplate.getTemplateName(), rqde);
		} catch (ReportTemplateDAOException rtde) {
			throw new ReportTemplateServiceException("Failed to update report template for " + reportTemplate.getTemplateName(), rtde);
		}

		return managedReportTemplate;
	}

	/**
	 * 
	 * @param reportTemplate
	 * @throws ReportTemplateServiceException
	 */
	private void deleteReportTemplate(BaseReportTemplate reportTemplate) throws ReportTemplateServiceException {
		try {
			reportTemplateDAO.delete(reportTemplate);
		} catch (ReportTemplateDAOException rtde) {
			throw new ReportTemplateServiceException("Failed to delete report template " + reportTemplate.getTemplateName(), rtde);
		}
	}

	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private BaseReportTemplate findReportTemplate(int reportTemplateId) throws ReportTemplateServiceException {
		try {
			return reportTemplateDAO.find(reportTemplateId);
		} catch (ReportTemplateDAOException rtde) {
			throw new ReportTemplateServiceException("Failed finding report template for id = " + reportTemplateId, rtde);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public AreaChartTemplate save(AreaChartTemplate reportTemplate) throws ReportTemplateServiceException {
		return (AreaChartTemplate) saveReportTemplate(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public BarChartTemplate save(BarChartTemplate reportTemplate) throws ReportTemplateServiceException {
		return (BarChartTemplate) saveReportTemplate(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ColumnChartTemplate save(ColumnChartTemplate reportTemplate) throws ReportTemplateServiceException {
		return (ColumnChartTemplate) saveReportTemplate(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public LineChartTemplate save(LineChartTemplate reportTemplate) throws ReportTemplateServiceException {
		return (LineChartTemplate) saveReportTemplate(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public PieChartTemplate save(PieChartTemplate reportTemplate) throws ReportTemplateServiceException {
		return (PieChartTemplate) saveReportTemplate(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public AreaChartTemplate update(AreaChartTemplate reportTemplate) throws ReportTemplateServiceException {
		return (AreaChartTemplate) updateReportTemplateWithEntityCacheSync(reportTemplate);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public AreaChartTemplate updateAreaChartTemplate(AreaChartTemplate reportTemplate) throws ReportTemplateServiceException {
		try {
			return (AreaChartTemplate) reportTemplateDAO.updateEntity(reportTemplate);
		} catch (ReportTemplateDAOException rtde) {
			throw new ReportTemplateServiceException("err", rtde);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(AreaChartTemplate reportTemplate) throws ReportTemplateServiceException {
		deleteReportTemplate(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AreaChartTemplate findAreaChartTemplate(int reportTemplateId) throws ReportTemplateServiceException {
		return (AreaChartTemplate) findReportTemplate(reportTemplateId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public BarChartTemplate update(BarChartTemplate reportTemplate) throws ReportTemplateServiceException {
		return (BarChartTemplate) updateReportTemplateWithEntityCacheSync(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(BarChartTemplate reportTemplate) throws ReportTemplateServiceException {
		deleteReportTemplate(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BarChartTemplate findBarChartTemplate(int reportTemplateId) throws ReportTemplateServiceException {
		return (BarChartTemplate) findReportTemplate(reportTemplateId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ColumnChartTemplate update(ColumnChartTemplate reportTemplate) throws ReportTemplateServiceException {
		return (ColumnChartTemplate) updateReportTemplateWithEntityCacheSync(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(ColumnChartTemplate reportTemplate) throws ReportTemplateServiceException {
		deleteReportTemplate(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ColumnChartTemplate findColumnChartTemplate(int reportTemplateId) throws ReportTemplateServiceException {
		return (ColumnChartTemplate) findReportTemplate(reportTemplateId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public LineChartTemplate update(LineChartTemplate reportTemplate) throws ReportTemplateServiceException {
		return (LineChartTemplate) updateReportTemplateWithEntityCacheSync(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(LineChartTemplate reportTemplate) throws ReportTemplateServiceException {
		deleteReportTemplate(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LineChartTemplate findLineChartTemplate(int reportTemplateId) throws ReportTemplateServiceException {
		return (LineChartTemplate) findReportTemplate(reportTemplateId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public PieChartTemplate update(PieChartTemplate reportTemplate) throws ReportTemplateServiceException {
		return (PieChartTemplate) updateReportTemplateWithEntityCacheSync(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(PieChartTemplate reportTemplate) throws ReportTemplateServiceException {
		deleteReportTemplate(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PieChartTemplate findPieChartTemplate(int reportTemplateId) throws ReportTemplateServiceException {
		return (PieChartTemplate) findReportTemplate(reportTemplateId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BaseReportTemplate> findAllReportTemplate() throws ReportTemplateServiceException {
		try {
			return reportTemplateDAO.findAll();
		} catch (ReportTemplateDAOException rtde) {
			throw new ReportTemplateServiceException("find all report template failed", rtde);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ReportQuery constructReportQuery(AreaChartTemplate template) throws ReportTemplateServiceException {
		return constructCartesianChartReportQuery(template);
	}

	/**
	 * {@inheritDoc}
	 */
	public ReportQuery constructReportQuery(BarChartTemplate template) throws ReportTemplateServiceException {
		return constructCartesianChartReportQuery(template);
	}

	/**
	 * {@inheritDoc}
	 */
	public ReportQuery constructReportQuery(ColumnChartTemplate template) throws ReportTemplateServiceException {
		return constructCartesianChartReportQuery(template);
	}

	/**
	 * {@inheritDoc}
	 */
	public ReportQuery constructReportQuery(LineChartTemplate template) throws ReportTemplateServiceException {
		return constructCartesianChartReportQuery(template);
	}

	/**
	 * {@inheritDoc}
	 */
	public ReportQuery constructReportQuery(PieChartTemplate template) throws ReportTemplateServiceException {
		ReportQuery reportQuery = null;

		if (template != null && template.getId() > 0 && template.getModelId() > 0) {
			try {
				Model model = modelDAO.find(template.getModelId());

				// obtain the required column based on template configuration
				List<String> requiredColumnList = constructPieChartTemplateRequiredFields(template);

				Map<String, String> aliasLookUpMap = constructAliasLookupMap(model, requiredColumnList);

				// for single table model
				if (Approach.SINGLE_TABLE.equals(model.getApproach())) {
					reportQuery = constructSimpleModelReportQuery((SimpleModel) model, aliasLookUpMap);
					reportQuery.setId(template.getId());
				} else if (Approach.JOIN_QUERY.equals(model.getApproach())) {
					reportQuery = constructJoinModelReportQuery(model, aliasLookUpMap);
					reportQuery.setId(template.getId());
				}

			} catch (ModelDAOException mde) {
				throw new ReportTemplateServiceException("Error finding model [" + template.getModelId() + "]", mde);
			}
		}

		return reportQuery;
	}

	/**
	 * 
	 * @param template
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private List<String> constructPieChartTemplateRequiredFields(PieChartTemplate template) throws ReportTemplateServiceException {
		List<String> requiredColumnList = new ArrayList<String>();

		String categoryField = template.getModelCategoryField();
		String dataField = template.getModelDataField();

		if (StringUtils.isNotBlank(categoryField) && StringUtils.isNotBlank(dataField)) {
			requiredColumnList.add(categoryField);
			requiredColumnList.add(dataField);
		} else {
			throw new ReportTemplateServiceException("Incomplete template configuration for " + template.getTemplateName());
		}

		return requiredColumnList;
	}

	/**
	 * 
	 * @param template
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	public ReportQuery constructCartesianChartReportQuery(CartesianChartTemplate template) throws ReportTemplateServiceException {
		ReportQuery reportQuery = null;

		if (template != null && template.getId() > 0 && template.getModelId() > 0) {
			try {
				Model model = modelDAO.find(template.getModelId());

				// obtain the required column based on template configuration
				List<String> requiredColumnList = constructCartesianChartTemplateRequiredFields(template);

				Map<String, String> aliasLookUpMap = constructAliasLookupMap(model, requiredColumnList);

				// for single table model
				if (Approach.SINGLE_TABLE.equals(model.getApproach())) {
					reportQuery = constructSimpleModelReportQuery((SimpleModel) model, aliasLookUpMap);
					reportQuery.setId(template.getId());
				} else if (Approach.JOIN_QUERY.equals(model.getApproach())) {
					reportQuery = constructJoinModelReportQuery(model, aliasLookUpMap);
					reportQuery.setId(template.getId());
				}

			} catch (ModelDAOException mde) {
				throw new ReportTemplateServiceException("Error finding model [" + template.getModelId() + "]", mde);
			}
		}

		return reportQuery;
	}

	/**
	 * 
	 * @param model
	 * @param aliasLookUpMap
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private ReportQuery constructJoinModelReportQuery(Model model, Map<String, String> aliasLookUpMap) throws ReportTemplateServiceException {

		ReportQuery rq = null;

		// obtained the joined query
		ModelQuery modelQuery = model.getQuery();

		if (modelQuery == null || StringUtils.isBlank(modelQuery.getValue())) {
			throw new ReportTemplateServiceException("empty joined query for model " + model.getName());
		}

		String joinedQuery = modelQuery.getValue();

		int fromIdx = joinedQuery.toLowerCase().indexOf("from ");
		int selectIdx = joinedQuery.toLowerCase().indexOf(SELECT_KEY);

		if (fromIdx < 0 || selectIdx < 0) {
			throw new ReportTemplateServiceException("invalid joined query [" + joinedQuery + "] for model " + model.getName());
		}

		String selectExpression = joinedQuery.substring(selectIdx + SELECT_KEY.length(), fromIdx);

		String[] expresses = selectExpression.split(QUERY_SEPERATOR);

		StringBuilder sb = new StringBuilder();

		int foundColumnCount = 0;

		// for each required column, look through the available column of model
		// query
		for (Map.Entry<String, String> entry : aliasLookUpMap.entrySet()) {

			for (int eIdx = 0; eIdx < expresses.length; eIdx++) {
				// if expression belongs to the required column, retain for
				// final query
				if (expresses[eIdx] != null && expresses[eIdx].contains(entry.getValue())) {

					// replace the expresses query alias with model alias
					sb.append(buildSelectArg(expresses[eIdx], entry)).append(QUERY_SEPERATOR);
					foundColumnCount++;

					break;
				}
			}
		}

		// if not all required column resolved, treated as error
		if (foundColumnCount != aliasLookUpMap.size()) {
			sb.setLength(0);
		}

		if (sb.length() > 0) {
			// truncate last ","
			sb.setLength(sb.length() - QUERY_SEPERATOR.length());

			// add prefix of "select "
			sb.insert(0, SELECT_KEY);
			// append the original from and where clause
			sb.append(" ").append(joinedQuery.substring(fromIdx));

			rq = new ReportQuery();
			rq.setQuery(sb.toString());
			rq.setDatasource(model.getDatasource());
		} else {
			throw new ReportTemplateServiceException("No match expression from custom query [" + joinedQuery + "]");
		}

		return rq;
	}

	private String buildSelectArg(String orginalStr, Map.Entry<String, String> entry) {

		StringBuilder sb = new StringBuilder();

		String lowerCaseStr = orginalStr.toLowerCase();

		int idx = lowerCaseStr.indexOf(AS_KEY);

		// if no " as "
		if (idx < 0) {

			if (StringUtils.isBlank(entry.getValue())) {
				LOG.info(entry.getValue() + " is invalid. Can't be replaced. use original value ");
				sb.append(orginalStr);
			} else {
				// take original value
				sb.append(orginalStr);
				// append with " as " followed by replacement of field with
				// alias name
				sb.append(AS_KEY).append(APOSTROPHE_KEY).append(entry.getKey()).append(APOSTROPHE_KEY);
			}
		} else {
			// take until end of " as "
			sb.append(orginalStr.substring(0, idx + AS_KEY.length()));
			// append with replacement of field with alias name
			sb.append(APOSTROPHE_KEY).append(entry.getKey()).append(APOSTROPHE_KEY);
		}

		return sb.toString();
	}

	/**
	 * 
	 * @param model
	 * @param aliasLookUpMap
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private ReportQuery constructSimpleModelReportQuery(SimpleModel model, Map<String, String> aliasLookUpMap) throws ReportTemplateServiceException {
		String tableName = model.getTable();

		if (StringUtils.isBlank(tableName)) {
			throw new ReportTemplateServiceException("empty table name for model " + model.getName());
		}

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, String> entry : aliasLookUpMap.entrySet()) {
			sb.append(" ").append(entry.getValue()).append(AS_KEY).append(APOSTROPHE_KEY).append(entry.getKey()).append(APOSTROPHE_KEY).append(QUERY_SEPERATOR);
		}

		// truncate the last ","
		if (sb.length() > 0) {
			sb.setLength(sb.length() - QUERY_SEPERATOR.length());
		}

		sb.insert(0, SELECT_KEY);
		sb.append(" FROM ").append(tableName);

		ReportQuery rq = new ReportQuery();

		rq.setQuery(sb.toString());
		rq.setDatasource(model.getDatasource());

		return rq;
	}

	/**
	 * 
	 * @param template
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private List<String> constructCartesianChartTemplateRequiredFields(CartesianChartTemplate template) throws ReportTemplateServiceException {
		List<String> requiredColumnList = new ArrayList<String>();

		// obtain the x-Axis label field
		String modelDataLabelField = template.getModelDataLabelField();
		// obtain the series data field
		String modelDataValueField = template.getModelDataValueField();
		// obtain the field storing the series name
		String modelSeriesGroupField = template.getModelSeriesGroupField();

		// only if both present
		if (StringUtils.isNotBlank(modelDataLabelField) && StringUtils.isNotBlank(modelDataValueField) && StringUtils.isNotBlank(modelSeriesGroupField)) {

			requiredColumnList.add(modelDataLabelField);
			requiredColumnList.add(modelDataValueField);
			requiredColumnList.add(modelSeriesGroupField);
		}

		if (requiredColumnList.isEmpty()) {
			throw new ReportTemplateServiceException("Incomplete template configuration for " + template.getTemplateName());
		}

		return requiredColumnList;
	}

	/**
	 * 
	 * @param model
	 * @param requiredColumnList
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private Map<String, String> constructAliasLookupMap(Model model, List<String> requiredColumnList) throws ReportTemplateServiceException {
		Map<String, String> aliasLookupMap = new HashMap<String, String>();

		List<AttributeMapping> attrList = model.getAttributeBindings();

		for (AttributeMapping attributeMapping : attrList) {

			// store those mapping required by template
			if (requiredColumnList.contains(attributeMapping.getAlias())) {
				aliasLookupMap.put(attributeMapping.getAlias(), attributeMapping.getReferencedColumn());
			}
		}

		if (aliasLookupMap.isEmpty()) {
			throw new ReportTemplateServiceException("Can't find required model attribute required from " + model.getName());
		}

		return aliasLookupMap;
	}

	@Override
	public ReportQuery findReportQuery(int templateId) throws ReportTemplateServiceException {
		try {
			return reportQueryDAO.find(templateId);
		} catch (ReportQueryDAOException e) {
			throw new ReportTemplateServiceException("Can't find report query with template id : " + templateId, e);
		}
	}

	@Override
	public String constructDataFieldValueQuery(Model model, String requiredColumn) throws ReportTemplateServiceException {
		String query = "";
		if (Approach.SINGLE_TABLE.equals(model.getApproach())) {
			query = constructSimpleModelDataFieldValueQuery((SimpleModel) model, requiredColumn);
		} else if (Approach.JOIN_QUERY.equals(model.getApproach())) {
			query = constructJoinedModelDataFieldValueQuery(model, requiredColumn);
		}
		return query;
	}

	private String constructSimpleModelDataFieldValueQuery(SimpleModel model, String requiredColumn) throws ReportTemplateServiceException {
		String tableName = model.getTable();
		if (StringUtils.isBlank(tableName)) {
			throw new ReportTemplateServiceException("empty table name for model " + model.getName());
		}
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_KEY);
		sb.append(DISTINCT + requiredColumn);
		sb.append(" FROM ").append(tableName);
		return sb.toString();

	}

	private String constructJoinedModelDataFieldValueQuery(Model model, String requiredColumn) throws ReportTemplateServiceException {
		ModelQuery modelQuery = model.getQuery();
		if (modelQuery == null || StringUtils.isBlank(modelQuery.getValue())) {
			throw new ReportTemplateServiceException("empty joined query for model " + model.getName());
		}
		String joinedQuery = modelQuery.getValue();
		int fromIdx = joinedQuery.toLowerCase().indexOf("from ");
		int selectIdx = joinedQuery.toLowerCase().indexOf(SELECT_KEY);
		if (fromIdx < 0 || selectIdx < 0) {
			throw new ReportTemplateServiceException("invalid joined query [" + joinedQuery + "] for model " + model.getName());
		}

		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_KEY);
		sb.append(DISTINCT + requiredColumn);
		sb.append(" ").append(joinedQuery.substring(fromIdx));
		return sb.toString();
	}
}
