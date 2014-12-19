package com.savvis.spirits.report.model.services;

import java.util.List;

import javax.ejb.Local;

import com.savvis.spirits.report.model.domain.DatabaseType;

@Local
public interface DatabaseTypeHandler {
	DatabaseType save(DatabaseType databaseType) throws DatabaseTypeHandlerException;
	void update(DatabaseType databaseType) throws DatabaseTypeHandlerException;
	void delete(DatabaseType databaseType) throws DatabaseTypeHandlerException;
	DatabaseType find(int id) throws DatabaseTypeHandlerException;
	List<DatabaseType> findAll() throws DatabaseTypeHandlerException;
}
