package org.reporte.model.service.impl;

import static org.reporte.common.util.CommonUtils.checkForNull;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.reporte.datasource.domain.ColumnMetadata;
import org.reporte.datasource.domain.Datasource;
import org.reporte.datasource.service.DatabaseTypeHandler;
import org.reporte.datasource.service.DatasourceHandler;
import org.reporte.datasource.service.JdbcClient;
import org.reporte.datasource.service.exception.JdbcClientException;
import org.reporte.model.dao.ModelDAO;
import org.reporte.model.dao.exception.ModelDAOException;
import org.reporte.model.domain.AttributeMapping;
import org.reporte.model.domain.Model;
import org.reporte.model.domain.SimpleModel;
import org.reporte.model.service.ModelService;
import org.reporte.model.service.exception.ModelServiceException;
import org.reporte.model.service.util.JoinQueryConverter;
import org.reporte.model.service.util.SelectFieldMatcher;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ModelServiceImpl implements ModelService {

	private static final String SELECT_QUERY = "SELECT * FROM %s";

	@Inject
	private ModelDAO modelDAO;
	@Inject
	private DatasourceHandler datasourceHandler;
	@Inject
	private DatabaseTypeHandler databaseTypeHandler;
	@Inject
	private JdbcClient jdbcClient;
	
	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public Model save(Model model) throws ModelServiceException {
		checkForNull(model, "model");
		
		try {
			return modelDAO.insert(model);
		} catch (ModelDAOException e) {
			throw new ModelServiceException("Failed to save given Model object - " + model, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void update(Model model) throws ModelServiceException {
		checkForNull(model, "model");
		
		try {
			modelDAO.update(model);
		} catch (ModelDAOException e) {
			throw new ModelServiceException("Failed to update Model with given information - " + model, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void delete(Model model) throws ModelServiceException {
		checkForNull(model, "model");
		
		try {
			modelDAO.delete(model);
		} catch (ModelDAOException e) {
			throw new ModelServiceException("Failed to delete Model with given id[" + model.getId() + "].", e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Model find(int id) throws ModelServiceException {
		try {
			return modelDAO.find(id);
		} catch (ModelDAOException e) {
			throw new ModelServiceException("Failed to find Model with given id[" + id + "].", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Model> findAll() throws ModelServiceException {
		try {
			return modelDAO.findAll();
		} catch (ModelDAOException e) {
			throw new ModelServiceException("Failed to get all existing Models from persistence layer.", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<Model> findAllOrderByDatasourceName() throws ModelServiceException{
		try {
			return modelDAO.findAllOrderByDatasourceName();
		} catch (ModelDAOException e) {
			throw new ModelServiceException("Failed to get all existing Models order by datasource name, model name from persistence layer.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DatasourceHandler getDatasourceHandler() {
		if (datasourceHandler == null) {
			throw new IllegalStateException("DatasourceHandler reference must not be null.");
		}
		return datasourceHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DatabaseTypeHandler getDatabaseTypeHandler() {
		if (databaseTypeHandler == null) {
			throw new IllegalStateException("DatabaseTypeHandler reference must not be null.");
		}
		return databaseTypeHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JdbcClient getJdbcClient() {
		if (jdbcClient == null) {
			throw new IllegalStateException("JdbcClient reference must not be null.");
		}
		return jdbcClient;
	}

	@Override
	public void updateModelQueryFromJoinQuery(Model model) throws ModelServiceException {
		JoinQueryConverter jqConverter = new JoinQueryConverter(model.getDatasource(), jdbcClient);
		
		CCJSqlParserManager pm = new CCJSqlParserManager();
		
		try {
			//1. expand all * to actual column
			net.sf.jsqlparser.statement.Statement statement = pm.parse(new StringReader(model.getQuery().getJoinQuery()));
			
			if (statement instanceof Select) {
				Select selectStatement = (Select) statement;
				selectStatement.getSelectBody().accept(jqConverter);
				
				model.getQuery().setValue(jqConverter.getConvertedQuery());
			}
			
			//3. construct the initial attribute mapping
			updateModelAttributeMapping(model);
		} catch (JSQLParserException e) {
			throw new ModelServiceException("Failed parsing sql ",e);
		}
	}

	@Override
	public void updateModelQueryFromSimpleQuery(Model model) throws ModelServiceException {
		String tableName = ((SimpleModel) model).getTable();
		JoinQueryConverter jqConverter = new JoinQueryConverter(model.getDatasource(), jdbcClient);
		
		CCJSqlParserManager pm = new CCJSqlParserManager();
		//1. construct into select * from table query string
		String query = String.format(SELECT_QUERY, tableName);
		try {
			
			//2. expand the * to actual column
			net.sf.jsqlparser.statement.Statement statement = pm.parse(new StringReader(query));
			
			if (statement instanceof Select) {
				Select selectStatement = (Select) statement;
				selectStatement.getSelectBody().accept(jqConverter);
				
				model.getQuery().setValue(jqConverter.getConvertedQuery());
			}
			
			//3. construct the initial attribute mapping
			updateModelAttributeMapping(model);

		} catch (JSQLParserException e) {
			throw new ModelServiceException("Failed parsing sql ",e);
		}
	}
	
	/**
	 * 
	 * @param model
	 * @throws ModelServiceException
	 */
	private void updateModelAttributeMapping(Model model) throws ModelServiceException{
		try {
			List<ColumnMetadata> columns = jdbcClient.getColumnsFromQuery(model.getDatasource(), model.getQuery().getValue());

			List<AttributeMapping> attributeMapingList = convertIntoAttributeMappings(columns);
			
			model.getAttributeBindings().clear();
			model.getAttributeBindings().addAll(attributeMapingList);
		} catch (JdbcClientException e) {
			throw new ModelServiceException("Failed getting columns for query ",e);
		}
	}
	/**
	 * 
	 * @param colList
	 * @return
	 */
	private List<AttributeMapping> convertIntoAttributeMappings(List<ColumnMetadata> colList) {
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

	@Override
	public List<String> getModelFieldUniqueValue(Model model, String aliasFieldName)
			throws ModelServiceException {
		
		List<String> uniqueValueList = new ArrayList<String>();
		SelectItem matchedSelectItem = null;

		//1. lookup the reference name
		String refFieldName = extractModelReferenceFieldName(model, aliasFieldName);
		
		if(refFieldName==null){
			throw new ModelServiceException("alias field name ["+aliasFieldName+"] not exist in model");
		}
		
		//2. create selectItemVisitor for reference field name
		SelectFieldMatcher matcher = new SelectFieldMatcher(refFieldName);

		CCJSqlParserManager pm = new CCJSqlParserManager();
		try {
			
			//3. parse model's query string
			net.sf.jsqlparser.statement.Statement statement = pm.parse(new StringReader(model.getQuery().getValue()));
			
			if (statement instanceof Select) {
				SelectBody selectBody = ((Select)statement).getSelectBody();
				
				if(selectBody instanceof PlainSelect){
					PlainSelect ps = (PlainSelect)selectBody;
					
					//4. for each selet item
					for(SelectItem si : ps.getSelectItems()){
						si.accept(matcher);
						
						//5. get matched selected item
						matchedSelectItem = matcher.getMatchedSelectItem();
						
						//6. if match found exit loop
						if(matchedSelectItem != null){
							break;
						}
					}
					
					//7. if match found, make it as unique select item
					if(matchedSelectItem!=null){
						ps.getSelectItems().clear();
						ps.getSelectItems().add(matchedSelectItem);
						ps.setDistinct(new Distinct());
						
						uniqueValueList.addAll(retrieveFieldUniqueValue(model.getDatasource(),statement.toString()));
					}
				}
			}
			
		} 
		catch (JSQLParserException e) {
			throw new ModelServiceException("parsing error",e);
		}
		catch(JdbcClientException e){
			throw new ModelServiceException("error retrieving field unique value", e);
		}
		
		return uniqueValueList;
	}
	
	/**
	 * 
	 * @param ds
	 * @param query
	 * @return
	 * @throws JdbcClientException 
	 */
	private List<String> retrieveFieldUniqueValue(Datasource ds, String query) throws JdbcClientException{
		
		List<String> uniqueValueList = new ArrayList<String>();
		
		List<Map<ColumnMetadata, String>> resultList = jdbcClient.execute(ds, query);
		
		for (Map<ColumnMetadata, String> map : resultList) {
			for(Map.Entry<ColumnMetadata, String> entry: map.entrySet()){
				uniqueValueList.add(entry.getValue());
			}
		}
		
		return uniqueValueList;
	}
	
	/**
	 * 
	 * @param model
	 * @param aliasFieldName
	 * @return
	 */
	private String extractModelReferenceFieldName(Model model, String aliasFieldName){
		
		String referenceFieldName = null;
		
		for(AttributeMapping attrMap : model.getAttributeBindings()){
			if(attrMap!=null && attrMap.getAlias().equals(aliasFieldName)){
				referenceFieldName = attrMap.getReferencedColumn();
				break;
			}
		}
		
		return referenceFieldName;
	}
}
