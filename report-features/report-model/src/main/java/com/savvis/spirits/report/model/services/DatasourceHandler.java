package com.savvis.spirits.report.model.services;

import java.util.List;

import javax.ejb.Local;

import com.savvis.spirits.report.model.domain.Datasource;

@Local
public interface DatasourceHandler {
	Datasource save(Datasource datasource) throws DatasourceHandlerException;
	void update(Datasource datasource) throws DatasourceHandlerException;
	void delete(Datasource datasource) throws DatasourceHandlerException;
	Datasource find(int id) throws DatasourceHandlerException;
	List<Datasource> findAll() throws DatasourceHandlerException;
}
