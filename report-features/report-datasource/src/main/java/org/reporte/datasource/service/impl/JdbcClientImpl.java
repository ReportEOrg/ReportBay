package org.reporte.datasource.service.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.reporte.common.domain.SqlTypeEnum;
import org.reporte.datasource.domain.ColumnMetadata;
import org.reporte.datasource.domain.Datasource;
import org.reporte.datasource.service.JdbcClient;
import org.reporte.datasource.service.exception.JdbcClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcClientImpl implements JdbcClient {
	private static final Logger LOG = LoggerFactory.getLogger(JdbcClientImpl.class);
	private static final int TABLE_NAME = 3;
	private static final String SELECT_ALL = "SELECT * FROM %s";

	// Note: Column index starts from 1
	private static final int DB_COLUMN_START_IDX = 1;
	
	private static final int MIN_ROW = 1;

	/**
	 * Apache Commons DBCP API helps us in getting rid of tightly coupleness to
	 * respective driver API by providing DataSource implementation that works
	 * as an abstraction layer between our program and different JDBC drivers.
	 * <p>
	 * 
	 * @param ds
	 * @return
	 */
	private DataSource getDatasource(Datasource ds) {
		LOG.trace("Getting datasource for " + ds.getName() + "..");

		BasicDataSource dbcpDs = new BasicDataSource();
		
		dbcpDs.setDriverClassName(ds.getType().getDriverName());
		dbcpDs.setUrl(String.format(ds.getType().getUrlPattern(), ds.getHostname(), ds.getPort(), ds.getSchema()));
		dbcpDs.setUsername(ds.getUsername());
		dbcpDs.setPassword(ds.getPassword());

		LOG.trace("Datasource returned.");
		return dbcpDs;
	}

	/**
	 * Release the resources.
	 * <p>
	 * 
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	private void release(DataSource ds, Connection conn, Statement stmt, ResultSet rs) {
		LOG.debug("Attempting to release resources after use..");
		if (rs != null) {
			LOG.trace("Closing Resultset..");
			try{
				rs.close();
				LOG.trace("Resultset closed.");
			}
			catch(Exception e){
				LOG.warn("Error releasing Resultset.", e);
			}
		}
		if (stmt != null) {
			LOG.trace("Closing Statement..");
			try{
				stmt.close();
				LOG.trace("Statement closed.");
			}
			catch(Exception e){
				LOG.warn("Error releasing Statement.", e);
			}
		}
		if (conn != null) {
			LOG.trace("Closing connection..");
			try{
				conn.close();
				LOG.trace("Connection closed.");
			}
			catch(Exception e){
				LOG.warn("Error releasing Connection.", e);
			}
		}
		
		if(ds instanceof BasicDataSource){
			try{
				((BasicDataSource)ds).close();
			}
			catch(Exception e){
				LOG.warn("Error closing Basic Data source.", e);
			}
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getTableNames(Datasource ds) throws JdbcClientException {
		LOG.debug("Getting existing metadata table names from the schema of given Database..");
		LOG.trace("Target Database Schema Details = {}", ds);
		Connection conn = null;
		ResultSet rs = null;
		DataSource dbcpDs = null;
		try {
			dbcpDs = getDatasource(ds);
			LOG.trace("Getting connection from DataSource..");
			conn = dbcpDs.getConnection();
			LOG.trace("Getting metadata table names..");
			rs = conn.getMetaData().getTables(null, null, "%", null);

			List<String> tableNames = new ArrayList<String>();
			while (rs.next()) {
				tableNames.add(rs.getString(TABLE_NAME));
			}
			LOG.trace("Table names returned: {}", tableNames);

			return tableNames;
		} catch (SQLException e) {
			throw new JdbcClientException("Failed to get metadata table names for given schema.", e);
		} finally {
			release(dbcpDs, conn, null, rs);
		}
	}

	@Override
	public List<ColumnMetadata> getColumns(Datasource ds, String tableName) throws JdbcClientException {
		LOG.debug("Getting existing metadata column names of the given table[" + tableName + "]..");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		DataSource dbcpDs = null;

		try {
			dbcpDs = getDatasource(ds);
			LOG.trace("Getting connection from DataSource..");
			conn = dbcpDs.getConnection();
			stmt = conn.createStatement();
			stmt.setMaxRows(MIN_ROW);
			LOG.trace("Getting all column names from Table - {}..", tableName);
			rs = stmt.executeQuery(String.format(SELECT_ALL, tableName));

			List<ColumnMetadata> columnNames = getColumnMetadata(rs);
			
			LOG.trace("Column names returned: {}", columnNames);

			return columnNames;
		} catch (SQLException e) {
			throw new JdbcClientException("Failed to get metadata table names for given schema.", e);
		} finally {
			release(dbcpDs, conn, stmt, rs);
		}
	}

	
	@Override
	public List<ColumnMetadata> getColumnsFromQuery(Datasource ds, String query) throws JdbcClientException {
		LOG.debug("Getting existing metadata column names of the given query[" + query + "]..");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		DataSource dbcpDs = null;

		try {
			dbcpDs = getDatasource(ds);
			LOG.trace("Getting connection from DataSource..");
			conn = dbcpDs.getConnection();
			stmt = conn.createStatement();
			stmt.setMaxRows(MIN_ROW);
			LOG.trace("Getting all column names from query - {}..", query);
			rs = stmt.executeQuery(query);
			
			List<ColumnMetadata> columnNames = getColumnMetadata(rs);
			
			LOG.trace("Column names returned: {}", columnNames);

			return columnNames;
		} catch (SQLException e) {
			throw new JdbcClientException("Failed to get metadata table names for given schema.", e);
		} finally {
			release(dbcpDs, conn, stmt, rs);
		}
	}

	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<Map<ColumnMetadata, String>> process(ResultSet rs) throws SQLException {
		List<Map<ColumnMetadata, String>> rows = new ArrayList<Map<ColumnMetadata, String>>();
		
		//get column name from result set
		List<ColumnMetadata> columns = getColumnMetadata(rs);
		
		// Populate the map.
		while (rs.next()) {
			Map<ColumnMetadata, String> row = new LinkedHashMap<ColumnMetadata, String>();
			for (ColumnMetadata column : columns) {
				row.put(column, rs.getString(column.getLabel()));
			}
			rows.add(row);
		}

		LOG.debug("({}) rows returned.", rows.size());
		return rows;
	}

	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<ColumnMetadata> getColumnMetadata(ResultSet rs) throws SQLException{
		List<ColumnMetadata> columns = new ArrayList<ColumnMetadata>();
		SqlTypeEnum sqlType;

		// Get the column names in result set first.
		ResultSetMetaData metadata = rs.getMetaData();
		int noOfCols = metadata.getColumnCount();
		
		for (int colIdx = DB_COLUMN_START_IDX; colIdx <= noOfCols; colIdx++) {
			
			sqlType = SqlTypeEnum.fromJavaSqlType(metadata.getColumnType(colIdx));
			columns.add(new ColumnMetadata(metadata.getColumnLabel(colIdx), 
											   sqlType==null? "": sqlType.name(), 
											   metadata.getColumnClassName(colIdx), 
											   colIdx));
		}
		
		return columns;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Map<ColumnMetadata, String>> execute(Datasource ds, String query) throws JdbcClientException {
		return execute(ds, query, -1);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Map<ColumnMetadata, String>> execute(Datasource ds, String query, int maxRow) throws JdbcClientException {
		LOG.trace("Target datasource - {}", ds.getName());
		LOG.trace("Query to be executed - {}", query);

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		DataSource dbcpDs = null;

		try {
			dbcpDs = getDatasource(ds);
			LOG.trace("Getting connection from DataSource..");
			conn = dbcpDs.getConnection();
			stmt = conn.createStatement();
			//limit result rows to max row defined
			if(maxRow>0){
				stmt.setMaxRows(maxRow);
			}
			LOG.debug("Executing given query in the respective database..");

			rs = stmt.executeQuery(query);

			LOG.debug("Query execution completed.");
			return process(rs);
		} catch (SQLException e) {
			throw new JdbcClientException("Query execution failed.", e);
		} finally {
			release(dbcpDs, conn, stmt, rs);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int findQueryCount(Datasource ds, String query) throws JdbcClientException {
		int resultCount = 0;
		
		if(query!=null){
			String tempQuery = query.toUpperCase();
			int idx = tempQuery.indexOf("FROM ");
			
			if(idx!=-1){
				tempQuery = "select count(*) "+query.substring(idx);
				
				Connection conn = null;
				Statement stmt = null;
				ResultSet rs = null;
				DataSource dbcpDs = null;
				try {
					dbcpDs = getDatasource(ds);
					LOG.trace("Getting connection from DataSource..");
					conn = dbcpDs.getConnection();
					stmt = conn.createStatement();
					
					rs = stmt.executeQuery(tempQuery);
					
					if(rs.next()){
						resultCount = rs.getInt(1);
					}
					
				} catch (SQLException e) {
					throw new JdbcClientException("Query execution failed.", e);
				} finally {
					release(dbcpDs, conn, stmt, rs);
				}
			}
		}
		
		return resultCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean testConnection(Datasource ds) {
		LOG.debug("Trying to connect to database - {}@{}:{}..", ds.getSchema(), ds.getHostname(), ds.getPort());
		boolean result = false;
		Connection conn = null;
		DataSource dataSource = null;
		try {
			dataSource = getDatasource(ds);
			conn = dataSource.getConnection();
			LOG.debug("Successfully established the connection.");
			result = true;
		} catch (SQLException e) {
			LOG.debug("Failed to establish the connection.", e);
		} finally {
			release(dataSource, conn, null, null);
		}
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getQuotedIdentifier(Datasource ds) throws JdbcClientException{
		String quotedIdentifier = null;
		
		Connection conn = null;
		DataSource dataSource = null;
		
		try{
			dataSource = getDatasource(ds);
			conn = dataSource.getConnection();
			DatabaseMetaData dbMetaData = conn.getMetaData();
			
			quotedIdentifier = dbMetaData.getIdentifierQuoteString();
			
			if(StringUtils.isBlank(quotedIdentifier)){
				throw new JdbcClientException("Invalid quoted identifier "+quotedIdentifier);
			}
		} 
		catch (SQLException e) {
			throw new JdbcClientException(e);
		}
		finally {
			release(dataSource, conn, null, null);
		}
		
		return quotedIdentifier;
	}
}
