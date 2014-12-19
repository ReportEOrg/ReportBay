package com.savvis.spirits.report.model.dao;

import java.util.List;

import com.savvis.spirits.report.model.domain.Datasource;

public interface DatasourceDAO {
	public Datasource insert(Datasource datasource) throws DatasourceDAOException;
	public void update(Datasource datasource) throws DatasourceDAOException;
	public void delete(Datasource datasource) throws DatasourceDAOException;
	public Datasource find(int id) throws DatasourceDAOException;
	public List<Datasource> findAll() throws DatasourceDAOException;
}
