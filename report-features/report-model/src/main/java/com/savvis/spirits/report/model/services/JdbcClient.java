package com.savvis.spirits.report.model.services;

import java.util.List;
import java.util.Map;

import com.savvis.spirits.report.model.domain.ColumnMetadata;
import com.savvis.spirits.report.model.domain.Datasource;

public interface JdbcClient {
	public List<String> getTableNames(Datasource ds) throws JdbcClientException;
	public List<ColumnMetadata> getColumns(Datasource ds, String tableName) throws JdbcClientException;
	public List<Map<ColumnMetadata, String>> execute(Datasource ds, String query) throws JdbcClientException;
	public boolean testConnection(Datasource ds);
}
