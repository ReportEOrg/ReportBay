package org.reportbay.model.service.impl;

import static org.reportbay.common.util.CommonUtils.checkForNull;

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
import org.reportbay.datasource.dao.DatasourceDAO;
import org.reportbay.datasource.dao.exception.DatasourceDAOException;
import org.reportbay.datasource.domain.ColumnMetadata;
import org.reportbay.datasource.domain.Datasource;
import org.reportbay.datasource.service.JdbcClient;
import org.reportbay.datasource.service.exception.JdbcClientException;
import org.reportbay.model.dao.ModelDAO;
import org.reportbay.model.dao.exception.ModelDAOException;
import org.reportbay.model.domain.AttributeMapping;
import org.reportbay.model.domain.Model;
import org.reportbay.model.domain.SimpleModel;
import org.reportbay.model.service.ModelService;
import org.reportbay.model.service.exception.ModelServiceException;
import org.reportbay.model.service.util.JoinQueryConverter;
import org.reportbay.model.service.util.SelectFieldMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ModelServiceImpl implements ModelService {
	private final Logger LOG = LoggerFactory.getLogger(ModelServiceImpl.class);

	private static final String SELECT_QUERY = "SELECT * FROM %s";

	@Inject
	private ModelDAO modelDAO;

	@Inject
	private DatasourceDAO dataSourceDAO;

	@Inject
	private JdbcClient jdbcClient;
	
	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public Model save(Model model) throws ModelServiceException {
		LOG.info("save");
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
		LOG.info("update");
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
		LOG.info("delete");
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
		LOG.info("find");
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
		LOG.info("findAll");
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
		LOG.info("findAllOrderByDatasourceName");
		try {
			return modelDAO.findAllOrderByDatasourceName();
		} catch (ModelDAOException e) {
			throw new ModelServiceException("Failed to get all existing Models order by datasource name, model name from persistence layer.", e);
		}
	}

	@Override
	public void updateModelQueryFromJoinQuery(Model model) throws ModelServiceException {
		JoinQueryConverter jqConverter = new JoinQueryConverter(getModelDataSource(model), jdbcClient);
		
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
		JoinQueryConverter jqConverter = new JoinQueryConverter(getModelDataSource(model), jdbcClient);
		
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
			List<ColumnMetadata> columns = jdbcClient.getColumnsFromQuery(getModelDataSource(model), model.getQuery().getValue());

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

		//autoclose string reader
		try(StringReader queryStringReader = new StringReader(model.getQuery().getValue())) {

			CCJSqlParserManager pm = new CCJSqlParserManager();
			//2. parse model's query string
			net.sf.jsqlparser.statement.Statement statement = pm.parse(queryStringReader);
			
			if (statement instanceof Select) {
				SelectBody selectBody = ((Select)statement).getSelectBody();
				
				if(selectBody instanceof PlainSelect){
					PlainSelect ps = (PlainSelect)selectBody;

					//3. create selectItemVisitor for reference field name
					String quotedIdentifier = jdbcClient.getQuotedIdentifier(getModelDataSource(model));
					SelectFieldMatcher matcher = new SelectFieldMatcher(refFieldName,quotedIdentifier);
					
					//4. for each select item
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
						
						uniqueValueList.addAll(retrieveFieldUniqueValue(getModelDataSource(model),statement.toString()));
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
	
	/**
	 * 
	 * @param model
	 * @return
	 * @throws ModelServiceException
	 */
	private Datasource getModelDataSource(Model model) throws ModelServiceException{
		try{
			
			if(model.getDatasource()==null){
				throw new ModelServiceException("Model does not contain datasource");
			}
			
			return dataSourceDAO.find(model.getDatasource().getId());
		}
		catch(DatasourceDAOException dde){
			throw new ModelServiceException("Error finding model ["+model.getId()+"] datasource", dde);
		}
	}
}
