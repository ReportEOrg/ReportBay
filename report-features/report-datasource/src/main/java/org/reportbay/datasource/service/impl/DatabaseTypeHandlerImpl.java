package org.reportbay.datasource.service.impl;

import static org.reportbay.common.util.CommonUtils.checkForNull;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.reportbay.datasource.dao.DatabaseTypeDAO;
import org.reportbay.datasource.dao.exception.DatabaseTypeDAOException;
import org.reportbay.datasource.domain.DatabaseType;
import org.reportbay.datasource.service.DatabaseTypeHandler;
import org.reportbay.datasource.service.exception.DatabaseTypeHandlerException;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DatabaseTypeHandlerImpl implements DatabaseTypeHandler {
	@Inject
	private DatabaseTypeDAO databaseTypeDAO;
	
	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public DatabaseType save(DatabaseType databaseType)
			throws DatabaseTypeHandlerException {
		checkForNull(databaseType, "databaseType");
		
		try {
			return databaseTypeDAO.insert(databaseType);
		} catch(DatabaseTypeDAOException e) {
			throw new DatabaseTypeHandlerException("Failed to save given DatabaseType object - " + databaseType, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void update(DatabaseType databaseType) throws DatabaseTypeHandlerException {
		checkForNull(databaseType, "databaseType");
		
		try {
			databaseTypeDAO.update(databaseType);
		} catch(DatabaseTypeDAOException e) {
			throw new DatabaseTypeHandlerException("Failed to update DatabaseType with given information - " + databaseType, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void delete(DatabaseType databaseType) throws DatabaseTypeHandlerException {
		checkForNull(databaseType, "databaseType");
		
		try {
			databaseTypeDAO.delete(databaseType);
		} catch(DatabaseTypeDAOException e) {
			throw new DatabaseTypeHandlerException("Failed to delete DatabaseType with given id[" + databaseType.getId() + "].", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DatabaseType find(int id) throws DatabaseTypeHandlerException {
		try {
			return databaseTypeDAO.find(id);
		} catch(DatabaseTypeDAOException e) {
			throw new DatabaseTypeHandlerException("Failed to find DatabaseType with given id[" + id + "].", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DatabaseType> findAll() throws DatabaseTypeHandlerException {
		try {
			return databaseTypeDAO.findAll();
		} catch(DatabaseTypeDAOException e) {
			throw new DatabaseTypeHandlerException("Failed to get all existing DatabaseTypes from persistence layer.", e);
		}
	}

}
