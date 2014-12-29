package org.reporte.model.dao;

import java.util.List;

import org.reporte.model.dao.exception.DatabaseTypeDAOException;
import org.reporte.model.domain.DatabaseType;

public interface DatabaseTypeDAO {
	/**
	 * 
	 * @param datasource
	 * @return
	 * @throws DatabaseTypeDAOException
	 */
	DatabaseType insert(DatabaseType datasource) throws DatabaseTypeDAOException;
	/**
	 * 
	 * @param datasource
	 * @throws DatabaseTypeDAOException
	 */
	void update(DatabaseType datasource) throws DatabaseTypeDAOException;
	/**
	 * 
	 * @param datasource
	 * @throws DatabaseTypeDAOException
	 */
	void delete(DatabaseType datasource) throws DatabaseTypeDAOException;
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DatabaseTypeDAOException
	 */
	DatabaseType find(int id) throws DatabaseTypeDAOException;
	/**
	 * 
	 * @return
	 * @throws DatabaseTypeDAOException
	 */
	List<DatabaseType> findAll() throws DatabaseTypeDAOException;
}
