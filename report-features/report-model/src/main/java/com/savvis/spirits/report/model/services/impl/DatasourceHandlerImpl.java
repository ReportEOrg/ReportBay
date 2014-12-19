package com.savvis.spirits.report.model.services.impl;

import static com.savvis.spirits.report.common.util.CommonUtils.checkForNull;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import com.savvis.spirits.report.model.dao.DatasourceDAO;
import com.savvis.spirits.report.model.dao.DatasourceDAOException;
import com.savvis.spirits.report.model.domain.Datasource;
import com.savvis.spirits.report.model.services.DatasourceHandler;
import com.savvis.spirits.report.model.services.DatasourceHandlerException;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DatasourceHandlerImpl implements DatasourceHandler {
	@Inject
	private DatasourceDAO datasourceDAO;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public Datasource save(Datasource datasource)
			throws DatasourceHandlerException {
		checkForNull(datasource, "datasource");
		
		try {
			return datasourceDAO.insert(datasource);
		} catch(DatasourceDAOException e) {
			throw new DatasourceHandlerException("Failed to save given Datasource object - " + datasource, e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void update(Datasource datasource) throws DatasourceHandlerException {
		checkForNull(datasource, "datasource");
		
		try {
			datasourceDAO.update(datasource);
		} catch(DatasourceDAOException e) {
			throw new DatasourceHandlerException("Failed to update Datasource with given information - " + datasource, e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void delete(Datasource datasource) throws DatasourceHandlerException {
		checkForNull(datasource, "datasource");
		
		try {
			datasourceDAO.delete(datasource);
		} catch(DatasourceDAOException e) {
			throw new DatasourceHandlerException("Failed to delete Datasource with given id[" + datasource.getId() + "].", e);
		}
	}

	@Override
	public Datasource find(int id) throws DatasourceHandlerException {
		try {
			return datasourceDAO.find(id);
		} catch(DatasourceDAOException e) {
			throw new DatasourceHandlerException("Failed to find Datasource with given id[" + id + "].", e);
		}
	}

	@Override
	public List<Datasource> findAll() throws DatasourceHandlerException {
		try {
			return datasourceDAO.findAll();
		} catch(DatasourceDAOException e) {
			throw new DatasourceHandlerException("Failed to get all existing Datasources from persistence layer.", e);
		}
	}

}
