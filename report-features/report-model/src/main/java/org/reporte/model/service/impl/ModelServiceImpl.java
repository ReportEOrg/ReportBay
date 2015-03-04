package org.reporte.model.service.impl;

import static org.reporte.common.util.CommonUtils.checkForNull;

import java.io.StringReader;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.Select;

import org.reporte.datasource.service.DatabaseTypeHandler;
import org.reporte.datasource.service.DatasourceHandler;
import org.reporte.datasource.service.JdbcClient;
import org.reporte.model.dao.ModelDAO;
import org.reporte.model.dao.exception.ModelDAOException;
import org.reporte.model.domain.Model;
import org.reporte.model.service.ModelService;
import org.reporte.model.service.exception.ModelServiceException;
import org.reporte.model.service.util.JoinQueryConverter;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ModelServiceImpl implements ModelService {
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
			net.sf.jsqlparser.statement.Statement statement = pm.parse(new StringReader(model.getQuery().getJoinQuery()));
			
			if (statement instanceof Select) {
				Select selectStatement = (Select) statement;
				selectStatement.getSelectBody().accept(jqConverter);
				
				model.getQuery().setValue(jqConverter.getConvertedQuery());
			}
		} catch (JSQLParserException e) {
			throw new ModelServiceException("Failed parsing sql ",e);
		}
		
	}

}
