package org.reporte.model.dao;

import java.util.List;

import org.reporte.model.dao.exception.DatasourceDAOException;
import org.reporte.model.domain.Datasource;

public interface DatasourceDAO {
	/**
	 * 
	 * @param datasource
	 * @return
	 * @throws DatasourceDAOException
	 */
	Datasource insert(Datasource datasource) throws DatasourceDAOException;
	/**
	 * 
	 * @param datasource
	 * @throws DatasourceDAOException
	 */
	void update(Datasource datasource) throws DatasourceDAOException;
	/**
	 * 
	 * @param datasource
	 * @throws DatasourceDAOException
	 */
	void delete(Datasource datasource) throws DatasourceDAOException;
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DatasourceDAOException
	 */
	Datasource find(int id) throws DatasourceDAOException;
	/**
	 * 
	 * @return
	 * @throws DatasourceDAOException
	 */
	List<Datasource> findAll() throws DatasourceDAOException;
}
