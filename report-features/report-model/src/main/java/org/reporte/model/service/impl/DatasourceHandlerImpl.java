package org.reporte.model.service.impl;

import static org.reporte.common.util.CommonUtils.checkForNull;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.reporte.model.dao.DatasourceDAO;
import org.reporte.model.dao.exception.DatasourceDAOException;
import org.reporte.model.domain.Datasource;
import org.reporte.model.service.DatasourceHandler;
import org.reporte.model.service.exception.DatasourceHandlerException;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DatasourceHandlerImpl implements DatasourceHandler {
	@Inject
	private DatasourceDAO datasourceDAO;
	
	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Datasource find(int id) throws DatasourceHandlerException {
		try {
			return datasourceDAO.find(id);
		} catch(DatasourceDAOException e) {
			throw new DatasourceHandlerException("Failed to find Datasource with given id[" + id + "].", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Datasource> findAll() throws DatasourceHandlerException {
		try {
			return datasourceDAO.findAll();
		} catch(DatasourceDAOException e) {
			throw new DatasourceHandlerException("Failed to get all existing Datasources from persistence layer.", e);
		}
	}

}
