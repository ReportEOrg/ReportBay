package org.reportbay.model.test.integration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.Context;

import junit.framework.TestCase;

import org.apache.openejb.api.LocalClient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reportbay.datasource.domain.ColumnMetadata;
import org.reportbay.datasource.domain.DatabaseType;
import org.reportbay.datasource.domain.Datasource;
import org.reportbay.datasource.service.DatabaseTypeHandler;
import org.reportbay.datasource.service.DatasourceHandler;
import org.reportbay.datasource.service.JdbcClient;
import org.reportbay.datasource.service.exception.DatabaseTypeHandlerException;
import org.reportbay.datasource.service.exception.DatasourceHandlerException;
import org.reportbay.datasource.service.exception.JdbcClientException;
import org.reportbay.model.domain.AttributeMapping;
import org.reportbay.model.domain.Model;
import org.reportbay.model.domain.ModelQuery;
import org.reportbay.model.domain.SimpleModel;
import org.reportbay.model.service.ModelService;
import org.reportbay.model.service.exception.ModelServiceException;

// Any clients annotated with @LocalClient will be scanned at deployment time 
// for usage of injection-related annotations
@LocalClient
public class ModelServiceIntegrationTest extends TestCase {
	@Inject
	private ModelService modelService;
	@Inject
	private DatasourceHandler datasourceHandler;
	@Inject
	private DatabaseTypeHandler databaseTypeHandler;
	@Inject
	private JdbcClient jdbcClient;

	private static int modelId = 0;
	private static int datasourceId = 0;
	private static EJBContainer container;
	
	// Test Configuration
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "password";
	private static final String DB_HOSTNAME = "localhost";
	private static final String DB_PORT = "3306";
	private static final String DB_TARGET_SCHEMA = "cloudmw";
	private static final String DB_REPORT_SCHEMA = "report-e";

	
	@BeforeClass
	protected void setUp() throws Exception {
		final Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.openejb.client.LocalInitialContextFactory");

		props.put("reportDS", "new://Resource?type=DataSource");
		props.put("reportDS.JdbcDriver", "com.mysql.jdbc.Driver");
		props.put("reportDS.jdbcUrl", String.format("jdbc:mysql://%s:%s/%s", DB_HOSTNAME, DB_PORT, DB_REPORT_SCHEMA));
		props.put("reportDS.UserName", DB_USERNAME);
		props.put("reportDS.Password", DB_PASSWORD);
		props.put("reportDS.JtaManaged", "true");

		container = EJBContainer.createEJBContainer(props);
		// The injection occurs via acquiring a LocalInitialContext via the
		// LocalInitialContextFactory and calling bind("inject", instance)
		// passing in the instantiated local client object:
		container.getContext().bind("inject", this);
	}

	@Test
	public void testModelService() {
		doTestInsert();
		doTestDelete();
	}

	private void doTestInsert() {
		// Was the EJB injected?
		assertTrue(modelService != null);
		
		Model model = new SimpleModel();
		model.setName("test_model_" + new Date());
		
		Datasource ds = new Datasource();
		ds.setName("test_datasource_" + new Date());
		ds.setHostname(DB_HOSTNAME);
		ds.setPort(DB_PORT);
		ds.setUsername(DB_USERNAME);
		ds.setPassword(DB_PASSWORD);
		ds.setSchema(DB_TARGET_SCHEMA);
		
		DatabaseType type = null;
		try {
			type = databaseTypeHandler.find(1);
		} catch (DatabaseTypeHandlerException e) {
			fail("Failed to load existing DatabaseType from database.");
		}
			
		ds.setType(type);
		try {
			ds = datasourceHandler.save(ds);
			datasourceId = ds.getId();
		} catch (DatasourceHandlerException e) {
			fail("Failed to save new Datasource.");
		}
		
		model.setDatasource(ds);
		String table = null;
		try {
			table = jdbcClient.getTableNames(ds).get(0);
		} catch (JdbcClientException e) {
			fail("Failed to get metadata table names.");
		}
		((SimpleModel)model).setTable(table);
		
		ModelQuery query = new ModelQuery();
		query.setValue(String.format("SELECT * FROM %s", table));
		model.setQuery(query);
				
		try {
			List<ColumnMetadata> columns = jdbcClient.getColumns(ds, table);
			List<AttributeMapping> attributeBindings = new ArrayList<AttributeMapping>();
			for (ColumnMetadata column : columns) {
				AttributeMapping am = new AttributeMapping();
				am.setAlias(column.getLabel());
				am.setReferencedColumn(column.getLabel());
				am.setOrder(column.getOrder());
				attributeBindings.add(am);
			}
			model.setAttributeBindings(attributeBindings);
		} catch (JdbcClientException e) {
			fail("Failed to retrieve metadata column names.");
			e.printStackTrace();
		}
		
		try {
			model = modelService.save(model);
			modelId = model.getId();
		} catch (ModelServiceException e) {
			fail("Failed to save new model.");
		}
	}

	private void doTestDelete() {
		assertTrue(modelService != null);

		try {
			Model model = modelService.find(modelId);
			modelService.delete(model);

			Datasource ds = datasourceHandler.find(datasourceId);
			datasourceHandler.delete(ds);
		} catch (DatasourceHandlerException e) {
			fail("Failed to delete existing datasource..");
		} catch (ModelServiceException e) {
			fail("Failed to delete existing model");
		}
	}

}