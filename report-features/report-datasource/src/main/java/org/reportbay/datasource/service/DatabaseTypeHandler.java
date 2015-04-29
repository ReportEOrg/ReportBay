package org.reportbay.datasource.service;

import java.util.List;

import javax.ejb.Local;

import org.reportbay.datasource.domain.DatabaseType;
import org.reportbay.datasource.service.exception.DatabaseTypeHandlerException;

@Local
public interface DatabaseTypeHandler {
	/**
	 * 
	 * @param databaseType
	 * @return
	 * @throws DatabaseTypeHandlerException
	 */
	DatabaseType save(DatabaseType databaseType) throws DatabaseTypeHandlerException;
	
	/**
	 * 
	 * @param databaseType
	 * @throws DatabaseTypeHandlerException
	 */
	void update(DatabaseType databaseType) throws DatabaseTypeHandlerException;
	
	/**
	 * 
	 * @param databaseType
	 * @throws DatabaseTypeHandlerException
	 */
	void delete(DatabaseType databaseType) throws DatabaseTypeHandlerException;
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DatabaseTypeHandlerException
	 */
	DatabaseType find(int id) throws DatabaseTypeHandlerException;
	
	/**
	 * 
	 * @return
	 * @throws DatabaseTypeHandlerException
	 */
	List<DatabaseType> findAll() throws DatabaseTypeHandlerException;
}
