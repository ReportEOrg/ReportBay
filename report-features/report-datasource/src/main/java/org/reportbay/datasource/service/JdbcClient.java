package org.reportbay.datasource.service;

import java.util.List;
import java.util.Map;

import org.reportbay.datasource.domain.ColumnMetadata;
import org.reportbay.datasource.domain.Datasource;
import org.reportbay.datasource.service.exception.JdbcClientException;

public interface JdbcClient {
	/**
	 * 
	 * @param ds
	 * @return
	 * @throws JdbcClientException
	 */
	List<String> getTableNames(Datasource ds) throws JdbcClientException;

	/**
	 * 
	 * @param ds
	 * @param tableName
	 * @return
	 * @throws JdbcClientException
	 */
	List<ColumnMetadata> getColumns(Datasource ds, String tableName) throws JdbcClientException;
	
	/**
	 * 
	 * @param ds
	 * @param query
	 * @return
	 * @throws JdbcClientException
	 */
	List<ColumnMetadata> getColumnsFromQuery(Datasource ds, String query) throws JdbcClientException;
	/**
	 * 
	 * @param ds
	 * @param query
	 * @return
	 * @throws JdbcClientException
	 */
	List<Map<ColumnMetadata, String>> execute(Datasource ds, String query) throws JdbcClientException;
	/**
	 * 
	 * @param ds
	 * @param query
	 * @param maxRow
	 * @return
	 * @throws JdbcClientException
	 */
	List<Map<ColumnMetadata, String>> execute(Datasource ds, String query, int maxRow) throws JdbcClientException;
	
	/**
	 * 
	 * @param ds
	 * @return
	 */
	boolean testConnection(Datasource ds);
	
	/**
	 * 
	 * @param ds
	 * @param query
	 * @return
	 * @throws JdbcClientException
	 */
	int findQueryCount(Datasource ds, String query) throws JdbcClientException;
	
	/**
	 * 
	 * @param ds
	 * @return
	 * @throws JdbcClientException
	 */
	String getQuotedIdentifier(Datasource ds) throws JdbcClientException;
}
