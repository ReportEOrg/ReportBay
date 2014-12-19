package com.savvis.spirits.report.model.dao;

import java.util.List;

import com.savvis.spirits.report.model.domain.DatabaseType;

public interface DatabaseTypeDAO {
	public DatabaseType insert(DatabaseType datasource) throws DatabaseTypeDAOException;
	public void update(DatabaseType datasource) throws DatabaseTypeDAOException;
	public void delete(DatabaseType datasource) throws DatabaseTypeDAOException;
	public DatabaseType find(int id) throws DatabaseTypeDAOException;
	public List<DatabaseType> findAll() throws DatabaseTypeDAOException;

}
