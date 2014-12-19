package com.savvis.spirits.report.model.services.impl;

import java.sql.Connection;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.savvis.spirits.report.model.domain.ColumnMetadata;
import com.savvis.spirits.report.model.domain.Datasource;
import com.savvis.spirits.report.model.services.JdbcClient;
import com.savvis.spirits.report.model.services.JdbcClientException;

public class JdbcClientImpl implements JdbcClient {
	private static final Logger LOG = LoggerFactory.getLogger(JdbcClientImpl.class);
	private static final int TABLE_NAME = 3;
	private static final String SELECT_ALL = "SELECT * FROM %s";

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
	private void release(Connection conn, Statement stmt, ResultSet rs) {
		try {
			LOG.debug("Attempting to release resources after use..");
			if (rs != null) {
				LOG.trace("Closing Resultset..");
				rs.close();
				LOG.trace("Resultset closed.");
			}
			if (stmt != null) {
				LOG.trace("Closing Statement..");
				stmt.close();
				LOG.trace("Statement closed.");
			}
			if (conn != null) {
				LOG.trace("Closing connection..");
				conn.close();
				LOG.trace("Connection closed.");
			}
		} catch (SQLException e) {
			LOG.warn("Error releasing resources.", e);
		}
	}

	@Override
	public List<String> getTableNames(Datasource ds) throws JdbcClientException {
		LOG.debug("Getting existing metadata table names from the schema of given Database..");
		LOG.trace("Target Database Schema Details = {}", ds);
		Connection conn = null;
		ResultSet rs = null;

		try {
			DataSource dbcpDs = getDatasource(ds);
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
			release(conn, null, rs);
		}
	}

	@Override
	public List<ColumnMetadata> getColumns(Datasource ds, String tableName) throws JdbcClientException {
		LOG.debug("Getting existing metadata column names of the given table[" + tableName + "]..");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			DataSource dbcpDs = getDatasource(ds);
			LOG.trace("Getting connection from DataSource..");
			conn = dbcpDs.getConnection();
			stmt = conn.createStatement();
			LOG.trace("Getting all column names from Table - {}..", tableName);
			rs = stmt.executeQuery(String.format(SELECT_ALL, tableName));
			ResultSetMetaData metadata = rs.getMetaData();
			List<ColumnMetadata> columnNames = new ArrayList<ColumnMetadata>();
			int noOfCols = metadata.getColumnCount();
			// Note: Column index starts from 1
			for (int i = 1; i <= noOfCols; i++) {
				columnNames.add(new ColumnMetadata(metadata.getColumnName(i), metadata.getColumnTypeName(i), metadata
						.getColumnClassName(i), i));
			}
			LOG.trace("Column names returned: {}", columnNames);

			return columnNames;
		} catch (SQLException e) {
			throw new JdbcClientException("Failed to get metadata table names for given schema.", e);
		} finally {
			release(conn, stmt, rs);
		}
	}

	private List<Map<ColumnMetadata, String>> process(ResultSet rs) throws SQLException {
		List<Map<ColumnMetadata, String>> rows = new ArrayList<Map<ColumnMetadata, String>>();

		// Get the column names in result set first.
		ResultSetMetaData metadata = rs.getMetaData();
		int noOfCols = metadata.getColumnCount();

		List<ColumnMetadata> columns = new ArrayList<ColumnMetadata>();
		for (int i = 1; i <= noOfCols; i++) {
			ColumnMetadata column = new ColumnMetadata();
			column.setLabel(metadata.getColumnLabel(i));
			column.setTypeName(metadata.getColumnTypeName(i));
			
			columns.add(column);
		}
		
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

	@Override
	public List<Map<ColumnMetadata, String>> execute(Datasource ds, String query) throws JdbcClientException {
		LOG.trace("Target datasource - {}", ds.getName());
		LOG.trace("Query to be executed - {}", query);

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			DataSource dbcpDs = getDatasource(ds);
			LOG.trace("Getting connection from DataSource..");
			conn = dbcpDs.getConnection();
			stmt = conn.createStatement();
			LOG.debug("Executing given query in the respective database..");
			// Remove ';' in the end if any
			query = StringUtils.removeEnd(query.trim(), ";");
			LOG.debug("Query before being executed - [{}]", query);

			rs = stmt.executeQuery(query);
			LOG.debug("Query execution completed.");
			return process(rs);
		} catch (SQLException e) {
			throw new JdbcClientException("Query execution failed.", e);
		} finally {
			release(conn, stmt, rs);
		}
	}

	@Override
	public boolean testConnection(Datasource ds) {
		LOG.debug("Trying to connect to database - {}@{}:{}..", ds.getSchema(), ds.getHostname(), ds.getPort());
		boolean result = false;
		Connection conn = null;

		try {
			conn = getDatasource(ds).getConnection();
			LOG.debug("Successfully established the connection.");
			result = true;
		} catch (SQLException e) {
			LOG.debug("Failed to establish the connection.", e);
		} finally {
			release(conn, null, null);
		}
		return result;
	}

}
