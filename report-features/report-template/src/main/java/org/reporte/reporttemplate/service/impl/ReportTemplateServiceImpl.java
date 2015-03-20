package org.reporte.reporttemplate.service.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.reporte.common.util.CommonUtils;
import org.reporte.common.util.SqlConstants;
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
import org.reporte.reporttemplate.domain.CrossTabTemplate;
import org.reporte.reporttemplate.domain.CrossTabTemplateDetail;
import org.reporte.reporttemplate.domain.GroupOrAggregate;
import org.reporte.reporttemplate.domain.LineChartTemplate;
import org.reporte.reporttemplate.domain.PieChartTemplate;
import org.reporte.reporttemplate.domain.ReportQuery;
import org.reporte.reporttemplate.domain.SqlFunction;
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
	public void deleteReportTemplate(BaseReportTemplate reportTemplate) throws ReportTemplateServiceException {
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
	public BaseReportTemplate findReportTemplate(int reportTemplateId) throws ReportTemplateServiceException {
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
	public Optional<CrossTabTemplate> save(CrossTabTemplate crossTabTemplate) throws ReportTemplateServiceException {
		try {
			//Check for illegal argument
			CommonUtils.checkForNull(crossTabTemplate, CrossTabTemplate.class.getSimpleName());
			// Construct the crosstab query and validate it
			Optional<ReportQuery> query = constructReportQuery(crossTabTemplate);
			if (query.isPresent()) {
				crossTabTemplate.setReportQuery(query.get());
				return Optional.of((CrossTabTemplate)saveReportTemplate(crossTabTemplate));
			}else {
				//Do not throw any error as reportQuery is constructed by application based on user selection.
				//End user should not be notified of such exception which will outline internal logic
				LOG.error("Unable to save CrossTabTemplate as ReportQuery is Null "+crossTabTemplate);
				return Optional.empty();
			}
		} catch(ReportTemplateServiceException e){
			throw e;
		} catch (Exception e) {
			throw new ReportTemplateServiceException("Error while saving CrossTab Template..", e);
		}
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

		if (template != null && template.getModelId() > 0) {
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

		if (template != null && template.getModelId() > 0) {
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
		int selectIdx = joinedQuery.toLowerCase().indexOf(SqlConstants.SELECT);

		if (fromIdx < 0 || selectIdx < 0) {
			throw new ReportTemplateServiceException("invalid joined query [" + joinedQuery + "] for model " + model.getName());
		}

		String selectExpression = joinedQuery.substring(selectIdx + SqlConstants.SELECT.length(), fromIdx);

		String[] expresses = selectExpression.split(SqlConstants.QUERY_SEPERATOR);

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
					sb.append(buildSelectArg(expresses[eIdx], entry)).append(SqlConstants.QUERY_SEPERATOR);
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
			sb.setLength(sb.length() - SqlConstants.QUERY_SEPERATOR.length());

			// add prefix of "select "
			sb.insert(0, SqlConstants.SELECT);
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

		int idx = lowerCaseStr.indexOf(SqlConstants.AS);

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
				sb.append(SqlConstants.AS).append(SqlConstants.APOSTROPHE).append(entry.getKey()).append(SqlConstants.APOSTROPHE);
			}
		} else {
			// take until end of " as "
			sb.append(orginalStr.substring(0, idx + SqlConstants.AS.length()));
			// append with replacement of field with alias name
			sb.append(SqlConstants.APOSTROPHE).append(entry.getKey()).append(SqlConstants.APOSTROPHE);
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
			sb.append(" ").append(entry.getValue()).append(SqlConstants.AS).append(SqlConstants.APOSTROPHE).append(entry.getKey()).append(SqlConstants.APOSTROPHE).append(SqlConstants.QUERY_SEPERATOR);
		}

		// truncate the last ","
		if (sb.length() > 0) {
			sb.setLength(sb.length() - SqlConstants.QUERY_SEPERATOR.length());
		}

		sb.insert(0, SqlConstants.SELECT);
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
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReportQuery findReportQuery(int templateId) throws ReportTemplateServiceException {
		try {
			return reportQueryDAO.find(templateId);
		} catch (ReportQueryDAOException e) {
			throw new ReportTemplateServiceException("Can't find report query with template id : " + templateId, e);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String constructDataFieldValueQuery(Model model, String requiredFieldAlias) throws ReportTemplateServiceException {
		String query = "";
		
		List<String> requiredColumnList = new ArrayList<String>();
		requiredColumnList.add(requiredFieldAlias);

		Map<String, String> aliasLookUpMap = constructAliasLookupMap(model, requiredColumnList);
		
		if (Approach.SINGLE_TABLE.equals(model.getApproach())) {
			query = constructSimpleModelDataFieldValueQuery((SimpleModel) model, aliasLookUpMap);
		} 
		else if (Approach.JOIN_QUERY.equals(model.getApproach())) {
			query = constructJoinedModelDataFieldValueQuery(model, aliasLookUpMap);
		}
		return query;
	}
	/**
	 * 
	 * @param model
	 * @param aliasLookUpMap
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private String constructSimpleModelDataFieldValueQuery(SimpleModel model, Map<String, String> aliasLookUpMap) throws ReportTemplateServiceException {
		String tableName = model.getTable();

		if (StringUtils.isBlank(tableName)) {
			throw new ReportTemplateServiceException("empty table name for model " + model.getName());
		}

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, String> entry : aliasLookUpMap.entrySet()) {
			sb.append(" ").append(entry.getValue()).append(SqlConstants.AS).append(SqlConstants.APOSTROPHE).append(entry.getKey()).append(SqlConstants.APOSTROPHE).append(SqlConstants.QUERY_SEPERATOR);
		}

		// truncate the last ","
		if (sb.length() > 0) {
			sb.setLength(sb.length() - SqlConstants.QUERY_SEPERATOR.length());
		}

		sb.insert(0, SqlConstants.SELECT+SqlConstants.DISTINCT);
		sb.append(" FROM ").append(tableName);

		return sb.toString();
	}
	/**
	 * 
	 * @param model
	 * @param aliasLookUpMap
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private String constructJoinedModelDataFieldValueQuery(Model model, Map<String, String> aliasLookUpMap) throws ReportTemplateServiceException {

		// obtained the joined query
		ModelQuery modelQuery = model.getQuery();

		if (modelQuery == null || StringUtils.isBlank(modelQuery.getValue())) {
			throw new ReportTemplateServiceException("empty joined query for model " + model.getName());
		}

		String joinedQuery = modelQuery.getValue();

		int fromIdx = joinedQuery.toLowerCase().indexOf("from ");
		int selectIdx = joinedQuery.toLowerCase().indexOf(SqlConstants.SELECT);

		if (fromIdx < 0 || selectIdx < 0) {
			throw new ReportTemplateServiceException("invalid joined query [" + joinedQuery + "] for model " + model.getName());
		}

		String selectExpression = joinedQuery.substring(selectIdx + SqlConstants.SELECT.length(), fromIdx);

		String[] expresses = selectExpression.split(SqlConstants.QUERY_SEPERATOR);

		StringBuilder sb = new StringBuilder();

		int foundColumnCount = 0;

		// for each required column, look through the available column of model query
		for (Map.Entry<String, String> entry : aliasLookUpMap.entrySet()) {

			for (int eIdx = 0; eIdx < expresses.length; eIdx++) {
				// if expression belongs to the required column, retain for
				// final query
				if (expresses[eIdx] != null && expresses[eIdx].contains(entry.getValue())) {

					// replace the expresses query alias with model alias
					sb.append(buildSelectArg(expresses[eIdx], entry)).append(SqlConstants.QUERY_SEPERATOR);
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
			sb.setLength(sb.length() - SqlConstants.QUERY_SEPERATOR.length());

			// add prefix of "select SqlConstants.DISTINCT "
			sb.insert(0, SqlConstants.SELECT+SqlConstants.DISTINCT);
			// append the original from and where clause
			sb.append(" ").append(joinedQuery.substring(fromIdx));
		} else {
			throw new ReportTemplateServiceException("No match expression from custom query [" + joinedQuery + "]");
		}

		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<ReportQuery> constructReportQuery(CrossTabTemplate crossTabTemplate)throws ReportTemplateServiceException {
		try {
			CommonUtils.checkForNull(crossTabTemplate, CrossTabTemplate.class.getSimpleName());
			return constructCrossTabReportQuery(crossTabTemplate);
		} catch (ReportTemplateServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ReportTemplateServiceException("Error Constructing ReportQuery", e);
		}
	}
	

	private Optional<ReportQuery> constructCrossTabReportQuery(CrossTabTemplate crossTabTemplate)throws ReportTemplateServiceException{
		LOG.info("Construct Cross Tab Report Query "+crossTabTemplate);
		CommonUtils.checkForNull(crossTabTemplate, "CrossTabTemplate");
		if (crossTabTemplate.getModelId()>0) {
			LOG.info("Construct CrossTab Query from the model ["+crossTabTemplate.getModelId()+"]");
			Model model = null;
			try {
				// Get the model for the given Id
				model = modelDAO.find(crossTabTemplate.getModelId());
				if (model==null) {
					throw new ReportTemplateServiceException("Model object is Null for the given Model Id ["+crossTabTemplate.getModelId()+"]");
				}
				ReportQuery reportQuery = new ReportQuery();
				//Set reportQueryId if the crosstabtemplate id is not null and have value
				reportQuery.setId(crossTabTemplate.getId());
				reportQuery.setDatasource(model.getDatasource());
				//for single table model
				Optional<String> query = constructCrossTabQuery(crossTabTemplate, model);
				if (query.isPresent()) {
					reportQuery.setQuery(query.get());
					//Return the not null ReportQuery object. If the object is null, Null pointerException will be thrown here
					return Optional.of(reportQuery);
				}else{
					LOG.error("Unable to Construct Query String for CrossTab Template "+crossTabTemplate);
					return Optional.empty();
				}
			} catch (ModelDAOException e) {
				throw new ReportTemplateServiceException("Error while retrieving Model for Id ["+crossTabTemplate.getModelId()+"]", e);
			}
		}else{
			throw new ReportTemplateServiceException("Model Object ID of CrossTabTemplate must be greater than ZERO");
		}
	}
	
	
	/**
	 * Parse the Select Query String and return {@link Select} object.
	 * @param {@link String} String representation of Query String
	 * @return {@link Optional}<{@link Select}> return a optional select object
	 * @throws throws exception when the Query is not of type select or Invalid SQL query.
	 */
	@Override
	public Optional<Select> parseSelectQuery(String query) throws ReportTemplateServiceException{
		CCJSqlParserManager sqlParser = new CCJSqlParserManager();
		try {
			Statement statement = sqlParser.parse(new StringReader(query));
			if (statement instanceof Select) {
				Select select = (Select) statement;
				return Optional.of(select);
			}else{
				throw new ReportTemplateServiceException("Query ["+query+"] must be of type SELECT in order to parse");
			}
		}catch(JSQLParserException e){
			throw new ReportTemplateServiceException("Error in Parsing Query ["+query+"]", e);
		}
	}
	
	/**
	 * deparse the implementation of {@link SelectBody} into string
	 * @param selectBody 
	 * @return {@link String} string representation of the Query
	 */
	public String deparseSelect(SelectBody selectBody){
		StringBuilder builder = new StringBuilder();
		ExpressionDeParser expressionDeParser = new ExpressionDeParser();
		expressionDeParser.setBuffer(builder);
		SelectDeParser selectDeParser = new SelectDeParser(expressionDeParser,builder);
		selectBody.accept(selectDeParser);
		return String.valueOf(selectDeParser.getBuffer());
	}

	private Optional<String> constructCrossTabQuery(CrossTabTemplate crossTabTemplate, Model model) throws ReportTemplateServiceException{
		LOG.info("Constructing Cross Tab Query");
		//Sort the given CrossTabTemplate Detail according to customer request
		//This will sort the groupby fields accordingly
		List<CrossTabTemplateDetail> crossTabDetails = crossTabTemplate.getCrossTabDetail();
		if (CollectionUtils.isEmpty(crossTabDetails)) {
			throw new ReportTemplateServiceException("CrossTabTemplateDetail List cannot be empty");
		}
		LOG.info(String.valueOf(crossTabDetails));
		Collections.sort(crossTabDetails, new CrossTabDetailsComparator());
		LOG.info("CrossTabTemplateDetail after sorting "+crossTabDetails);
		List<AttributeMapping> attributeMappings = model.getAttributeBindings();
		if (CollectionUtils.isEmpty(attributeMappings)) {
			throw new ReportTemplateServiceException("Attribute Mappings object is Empty for the Model Id "+model.getId());
		}
		ModelQuery modelQuery = model.getQuery();
		if (modelQuery ==null) {
			throw new ReportTemplateServiceException("ModelQuery Object cannot be Null for the Model Id "+model.getId());
		}
		//Validate for empty/null query in model object
		if (StringUtils.isBlank(modelQuery.getValue())) {
			throw new ReportTemplateServiceException("Query String on ModelQuery Object cannot be Empty/Null/Blank");
		}
		LOG.info("Model Query "+ modelQuery.getValue());
		Optional<Select> select = parseSelectQuery(modelQuery.getValue());
		if (!select.isPresent()) {
			throw new ReportTemplateServiceException("Unable to parse Query "+ modelQuery.getValue());
		}
		SelectBody selectBody = select.get().getSelectBody();
		if (selectBody == null) {
			throw new ReportTemplateServiceException("SelectBody for the Query ["+modelQuery.getValue()+"] cannot be Null");
		}
		if (selectBody instanceof PlainSelect) {
			List<SelectItem> selects = new ArrayList<SelectItem>();
			List<Expression> groupByColumns = new ArrayList<Expression>();
			LOG.info("SelectBody of Type PlainSelect ["+selectBody+"]");
			PlainSelect plainSelect = (PlainSelect) selectBody;
			for (CrossTabTemplateDetail crossTabDetail : crossTabDetails) {
				Distinct distinct = plainSelect.getDistinct();
				//If SqlConstants.DISTINCT object is not null, there is SqlConstants.DISTINCT clause in the select query
				if (distinct!=null) {
					LOG.info("Parsing SqlConstants.DISTINCT Value from the Query");
					List<SelectItem> distinctSelectItems = distinct.getOnSelectItems();
					if (CollectionUtils.isNotEmpty(distinctSelectItems)) {
						LOG.info("SqlConstants.DISTINCT Items "+distinctSelectItems);
						LOG.info("Number of Distinct items "+distinctSelectItems.size());
						for (SelectItem selectItem : distinctSelectItems) {
							LOG.info("Select Item on SqlConstants.DISTINCT ["+selectItem+"]");
							// TODO: Need to implement logic for SqlConstants.DISTINCT items
							throw new UnsupportedOperationException("DISTINCT operation is not supported now");
						}
					}
				}
				List<SelectItem> selectItems = plainSelect.getSelectItems();
				if(CollectionUtils.isNotEmpty(selectItems)){
					LOG.info("Iterating Select Items");
					//Loop through each Select Item to construct the list of Select fields
					/*for (SelectItem selectItem : selectItems) {
						LOG.info("Select Field ["+selectItem+"]");
						if (selectItem instanceof AllColumns) {
							//TODO: temporarily commented out AllColumns. The query will always be expanded and this scenario will never occure
							LOG.info("SelectItem instance of AllColumns");
							//Construct select criteria from the attribute binding list
							// for simple model, the select field will be "*"
							if (model.getApproach().equals(Approach.SINGLE_TABLE)) {
								constructSimpleSelect(crossTabDetails, attributeMappings,plainSelect);
								// break the loop as single table will contain only one instance of AllColumns
								break;
							}
						}else if(selectItem instanceof AllTableColumns){
							//We assume if the SelectItem is instance of AllColumns, there will be only one selectItem. Hence we can break the loop if neccessary
							throw new UnsupportedOperationException("Yet to implement AllTableColumns operation on SelectItem");
						}else if(selectItem instanceof SelectExpressionItem){
							throw new UnsupportedOperationException("Yet to implement SelectExpression operation on SelectItem");
						}
					}*/
					Optional<SelectExpressionItem> selectItem = getSelectItem(crossTabDetail, selectItems);
					if (selectItem.isPresent()) {
						LOG.info("Computed SelectItem is not empty");
						//check if the crosstabdetail is of aggregate or grouping
						if (crossTabDetail.getGroupOrAggregate().equals(GroupOrAggregate.AGGREGATE)){
							//construct a aggregate function of select item
							selects.add(constructAggregate(selectItem.get().getExpression(), crossTabDetail.getSqlFunction()));
						}else if (crossTabDetail.getGroupOrAggregate().equals(GroupOrAggregate.GROUPING)) {
							//Add the selectItem to the list
							selects.add(selectItem.get());
							//Construct a grouping item
							groupByColumns.add(selectItem.get().getExpression());
						}
					}
				}else{
					throw new ReportTemplateServiceException("SelectItems list is empty for the query ["+modelQuery.getValue()+"]");
				}
			}
			//Modify the plainselect with new select and group by function
			plainSelect.setSelectItems(selects);
			plainSelect.setGroupByColumnReferences(groupByColumns);
			//Deparse the query to generate the new Model query for cross tab
			return Optional.of(deparseSelect(plainSelect));
		}
		return Optional.empty();
	}
	
	private SelectExpressionItem constructAggregate(Expression expression,SqlFunction sqlFunction){
		SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
		Function function = new Function();
		selectExpressionItem.setExpression(function);
		//Set the Aggregate Function Name
		function.setName(sqlFunction.name());
		ExpressionList expressionList = new ExpressionList();
		function.setParameters(expressionList);
		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(expression);
		expressionList.setExpressions(expressions);
		LOG.info("Constructed Aggregate Function "+selectExpressionItem);
		return selectExpressionItem;
		
	}
	
	@SuppressWarnings("rawtypes")
	private void constructSimpleSelect(List<CrossTabTemplateDetail> crossTabDetails, List<AttributeMapping> attributeMappings,PlainSelect plainSelect) throws ReportTemplateServiceException{
		LOG.info("Constructing Select Criteria");
		Map<String, List> selectCriteria = new HashMap<String, List>();
		//Loop through each crosstabdetail against list of attribute mapping in order to find the matching column name
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		List<Expression> groupByColumns = new ArrayList<Expression>();
		for (CrossTabTemplateDetail  crossTabDetail: crossTabDetails) {
			LOG.info(String.valueOf(crossTabDetail));
			//If the fiedl canno tbe found in the list of attribute Mappings.Then throw error.
			boolean fieldMatched = false;
			for (AttributeMapping attributeMapping : attributeMappings) {
				LOG.info("AttributeMapping "+ attributeMapping.getReferencedColumn());
				if (crossTabDetail.getModelAttributeName().equalsIgnoreCase(attributeMapping.getReferencedColumn())) {
					fieldMatched=true;
					LOG.info("Attribute Name ["+crossTabDetail.getModelAttributeName()+"] Matched");
					//Construct the select clause with simple column name
					SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
					//Create a column object and set it to selectexpression
					Column column = new Column();
					column.setColumnName(crossTabDetail.getModelAttributeName());
					//check if the column is of type grouping or aggregate
					if (crossTabDetail.getGroupOrAggregate().equals(GroupOrAggregate.GROUPING)) {
						LOG.info("Constructing Grouping for field "+crossTabDetail.getModelAttributeName());
						selectExpressionItem.setExpression(column);
						//For groupby, no need to create an new object. add the same column object to groupbyreference list in plain select.
						//Add the expressionItem to list of selected items
						selectItems.add(selectExpressionItem);
						//Add the column to groupby list
						groupByColumns.add(column);
					}else if(crossTabDetail.getGroupOrAggregate().equals(GroupOrAggregate.AGGREGATE)){
						LOG.info("Constructing Aggregate Function for field "+crossTabDetail.getModelAttributeName());
						Function function = new Function();
						selectExpressionItem.setExpression(function);
						//Set the Aggregate Function Name
						function.setName(crossTabDetail.getSqlFunction().name());
						ExpressionList expressionList = new ExpressionList();
						function.setParameters(expressionList);
						List<Expression> expressions = new ArrayList<Expression>();
						expressions.add(column);
						expressionList.setExpressions(expressions);
						selectItems.add(selectExpressionItem);
					}
					
				}
			}
			if (!fieldMatched) {
				throw new ReportTemplateServiceException("Cross Tab Field Name ["+crossTabDetail.getModelAttributeName()+"] cannot be matched in any of the attributeMappings "+attributeMappings);
			}
		}
		//Append select and group by item on the plainSelect object
		plainSelect.setSelectItems(selectItems);
		plainSelect.setGroupByColumnReferences(groupByColumns);
		LOG.info("PlainSelect after constructing Select and GroupBy clause ["+plainSelect+"]");
	}
	/**
	 * <p>
	 * Get the {@link SelectItem} for the given {@link CrossTabTemplateDetail}. Compare the model attribute name against the
	 * {@link Expression}. If the name is equal to Alias, the selectItem is returned. Otherwise the entire field name is 
	 * compared against the model attribute name
	 *  </p>
	 * @param crossTabDetail
	 * @param selectItems
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private Optional<SelectExpressionItem> getSelectItem(CrossTabTemplateDetail crossTabDetail, List<SelectItem> selectItems) throws ReportTemplateServiceException{
		LOG.info("Find attribute mappings for complex select query");
		LOG.info(String.valueOf(crossTabDetail));
		//If the fiedl canno tbe found in the list of attribute Mappings.Then throw error.
		boolean fieldMatched = false;
		for (SelectItem selectItem : selectItems) {
			LOG.info("SelectItem "+ selectItem);
			if (selectItem instanceof AllColumns) {
				//We assume there will be no instance of AllColumns as the query will be expanded during model creation itself for complex select
				throw new UnsupportedOperationException("Query should expand AllColumns during Model Creation ");
			}else if(selectItem instanceof AllTableColumns){
				//We assume there will be no instance of AllColumns as the query will be expanded during model creation itself
				throw new UnsupportedOperationException("Query should expand AllTableColumns during Model Creation ");
			}else if(selectItem instanceof SelectExpressionItem){
				LOG.info("SelectItem instance of SelectExpressionItem");
				SelectExpressionItem expressionItem = (SelectExpressionItem) selectItem;
				//check SelectExpressionItem for alias name. If alias name matches, then add this to filteredSelect
				Alias alias = expressionItem.getAlias();
				if (alias!=null) {
					LOG.info(String.valueOf(alias));
					//compare alias name with CrossTabTemplateDetail column name
					if(crossTabDetail.getModelAttributeName().equalsIgnoreCase(alias.getName())){
						fieldMatched = true;
						LOG.info("Attribute Name matched with Alias "+alias.getName());
						return Optional.of(expressionItem);
					}
				}
				Expression expression = expressionItem.getExpression();
				if (expression!=null) {
					//check Expression object for instance of
					if (expression instanceof Column) {
						LOG.info("Expression is of type Column "+expression);
						//Check the column name against attribute name for match
						if (((Column) expression).getColumnName().equalsIgnoreCase(crossTabDetail.getModelAttributeName())) {
							LOG.info("Attribute Name is matched against Column "+expression);
							fieldMatched= true;
							return Optional.of(expressionItem);
						}
						
					}else if (expression instanceof Function){
						LOG.info("Expression is of type Function "+expression);
						//Check the attribute name against whole field name
						//construct field name
						StringBuilder builder = new StringBuilder();
						ExpressionDeParser deParser = new ExpressionDeParser();
						deParser.setBuffer(builder);
						expression.accept(deParser);
						LOG.info("Deparsed Expression object "+deParser.getBuffer());
						//check the full name against the constructed deparser builder
						if (crossTabDetail.getModelAttributeName().equalsIgnoreCase(String.valueOf(deParser.getBuffer()))) {
							LOG.info("Attribute Name is matched against Function "+expression);
							fieldMatched = true;
							return Optional.of(expressionItem);
						}
					}else if (expression instanceof SubSelect){
						LOG.info("Expression is of type SubSelect "+expression);
						//Check the attribute name against whole field name
						//construct field name
						StringBuilder builder = new StringBuilder();
						ExpressionDeParser deParser = new ExpressionDeParser();
						deParser.setBuffer(builder);
						expression.accept(deParser);
						LOG.info("Deparsed Expression object "+deParser.getBuffer());
						//check the full name against the constructed deparser builder
						if (crossTabDetail.getModelAttributeName().equalsIgnoreCase(String.valueOf(deParser.getBuffer()))) {
							LOG.info("Attribute Name is matched against SubSelect "+expression);
							fieldMatched = true;
							return Optional.of(expressionItem);
						}
					}
				}
			}
		}
		if (!fieldMatched) {
			throw new ReportTemplateServiceException("Cross Tab Field Name ["+crossTabDetail.getModelAttributeName()+"] cannot be matched in any of the attributeMappings "+selectItems);
		}
		return Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<CrossTabTemplate> update(CrossTabTemplate reportTemplate) throws ReportTemplateServiceException {
		LOG.info("Updating CrossTab Template "+reportTemplate);
		try {
			// Reconstruct the ReportQuery if the template have been modified
			Optional<ReportQuery> reportQuery = constructReportQuery(reportTemplate);
			if (!reportQuery.isPresent()) {
				LOG.error("ReportQuery object is null");
			}else{
				reportTemplate.setReportQuery(reportQuery.get());
			}
			return Optional.of((CrossTabTemplate) updateReportTemplateWithEntityCacheSync(reportTemplate));
		} catch(ReportTemplateServiceException e){
			throw e;
		} catch (Exception e) {
			throw new ReportTemplateServiceException("Error while updating CrossTab Template..", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(CrossTabTemplate reportTemplate) throws ReportTemplateServiceException {
		LOG.info("Deleting CrossTab Template "+reportTemplate);
		try {
			deleteReportTemplate(reportTemplate);
		} catch (ReportTemplateServiceException e) {
			throw e;
		} catch(Exception e){
			throw new ReportTemplateServiceException("Error deleting CrossTabTemplate..", e);
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<CrossTabTemplate> findCrossTabTemplate(int reportTemplateId) throws ReportTemplateServiceException {
		LOG.info("Finding CrossTab Template for the ID "+reportTemplateId);
		try {
			BaseReportTemplate reportTemplate = findReportTemplate(reportTemplateId);
			if (reportTemplate ==null) {
				return Optional.empty();
			}
			if (reportTemplate instanceof CrossTabTemplate) {
				return Optional.of((CrossTabTemplate) reportTemplate);
			}else {
				throw new ReportTemplateServiceException("Report Template ID should be of CrossTab Template Object");
			}
		} catch(ReportTemplateServiceException e){
			throw e;
		} catch (Exception e) {
			throw new ReportTemplateServiceException("Error in finding CrossTab report with Template Id "+reportTemplateId, e);
		}
	}

}
