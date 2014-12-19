package com.savvis.spirits.report.model.services.impl;

import static com.savvis.spirits.report.common.util.CommonUtils.checkForNull;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import com.savvis.spirits.report.model.dao.ModelDAO;
import com.savvis.spirits.report.model.dao.ModelDAOException;
import com.savvis.spirits.report.model.domain.Model;
import com.savvis.spirits.report.model.services.JdbcClient;
import com.savvis.spirits.report.model.services.DatabaseTypeHandler;
import com.savvis.spirits.report.model.services.DatasourceHandler;
import com.savvis.spirits.report.model.services.ModelService;
import com.savvis.spirits.report.model.services.ModelServiceException;

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

	@Override
	public Model find(int id) throws ModelServiceException {
		try {
			return modelDAO.find(id);
		} catch (ModelDAOException e) {
			throw new ModelServiceException("Failed to find Model with given id[" + id + "].", e);
		}
	}

	@Override
	public List<Model> findAll() throws ModelServiceException {
		try {
			return modelDAO.findAll();
		} catch (ModelDAOException e) {
			throw new ModelServiceException("Failed to get all existing Models from persistence layer.", e);
		}
	}

	@Override
	public DatasourceHandler getDatasourceHandler() {
		if (datasourceHandler == null) {
			throw new IllegalStateException("DatasourceHandler reference must not be null.");
		}
		return datasourceHandler;
	}

	@Override
	public DatabaseTypeHandler getDatabaseTypeHandler() {
		if (databaseTypeHandler == null) {
			throw new IllegalStateException("DatabaseTypeHandler reference must not be null.");
		}
		return databaseTypeHandler;
	}

	@Override
	public JdbcClient getJdbcClient() {
		if (jdbcClient == null) {
			throw new IllegalStateException("JdbcClient reference must not be null.");
		}
		return jdbcClient;
	}

}
