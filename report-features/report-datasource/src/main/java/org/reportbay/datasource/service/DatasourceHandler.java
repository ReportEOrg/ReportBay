package org.reportbay.datasource.service;

import java.util.List;

import javax.ejb.Local;

import org.reportbay.datasource.domain.Datasource;
import org.reportbay.datasource.service.exception.DatasourceHandlerException;

@Local
public interface DatasourceHandler {
	/**
	 * 
	 * @param datasource
	 * @return
	 * @throws DatasourceHandlerException
	 */
	Datasource save(Datasource datasource) throws DatasourceHandlerException;
	/**
	 * 
	 * @param datasource
	 * @throws DatasourceHandlerException
	 */
	void update(Datasource datasource) throws DatasourceHandlerException;
	/**
	 * 
	 * @param datasource
	 * @throws DatasourceHandlerException
	 */
	void delete(Datasource datasource) throws DatasourceHandlerException;
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DatasourceHandlerException
	 */
	Datasource find(int id) throws DatasourceHandlerException;
	/**
	 * 
	 * @return
	 * @throws DatasourceHandlerException
	 */
	List<Datasource> findAll() throws DatasourceHandlerException;
}
