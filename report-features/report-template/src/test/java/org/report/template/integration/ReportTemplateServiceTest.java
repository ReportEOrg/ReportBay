package org.report.template.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingException;

import net.sf.jsqlparser.statement.select.Select;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reportbay.common.domain.SqlTypeEnum;
import org.reportbay.reporttemplate.dao.ReportTemplateDAO;
import org.reportbay.reporttemplate.domain.CrossTabFieldType;
import org.reportbay.reporttemplate.domain.CrossTabTemplate;
import org.reportbay.reporttemplate.domain.CrossTabTemplateDetail;
import org.reportbay.reporttemplate.domain.GroupOrAggregate;
import org.reportbay.reporttemplate.domain.ReportTemplateTypeEnum;
import org.reportbay.reporttemplate.domain.SqlFunction;
import org.reportbay.reporttemplate.service.ReportTemplateService;
import org.reportbay.reporttemplate.service.exception.ReportTemplateServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportTemplateServiceTest{
	
	private static final Logger LOG = LoggerFactory.getLogger(ReportTemplateServiceTest.class);
	
	private static EJBContainer ejbContainer;

	@Inject
	private ReportTemplateService reportTemplateService;
	
	@Inject
	private ReportTemplateDAO reportTemplateDAO;
	
	// Test Configuration
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "password";
	private static final String DB_HOSTNAME = "localhost";
	private static final String DB_PORT = "3306";
	private static final String DB_REPORT_SCHEMA = "reporte";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		final Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.openejb.client.LocalInitialContextFactory");

		props.put("reportDS", "new://Resource?type=DataSource");
		props.put("reportDS.JdbcDriver", "com.mysql.jdbc.Driver");
		props.put("reportDS.jdbcUrl", String.format("jdbc:mysql://%s:%s/%s", DB_HOSTNAME, DB_PORT, DB_REPORT_SCHEMA));
		props.put("reportDS.UserName", DB_USERNAME);
		props.put("reportDS.Password", DB_PASSWORD);
		props.put("reportDS.JtaManaged", "true");

		ejbContainer = EJBContainer.createEJBContainer(props);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// Close the Embeddable EJB Container, releasing all resources 
		if (ejbContainer!=null) {
			LOG.info("Closing down EJB container");
			ejbContainer.close();
		}
	}

	@Before
	public void setUp() throws NamingException {
		ejbContainer.getContext().bind("inject", this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws ReportTemplateServiceException {
		LOG.info("Successfully launching test case");
		Optional<Select> select = reportTemplateService.parseSelectQuery("Select * from Customer");
		LOG.info(String.valueOf(select.get()));
	}
	
	@Test
	public void addCrossTabRecord()throws ReportTemplateServiceException {
		LOG.info("Adding CrossTab Template Record");
		CrossTabTemplate crossTabTemplate = new CrossTabTemplate();
		List<CrossTabTemplateDetail> list = new ArrayList<CrossTabTemplateDetail>();
		crossTabTemplate.setModelId(1);
		crossTabTemplate.setReportDisplayName("CrossTabTemplate Test 1");
		crossTabTemplate.setReportTemplateType(ReportTemplateTypeEnum.SIMPLE);
		crossTabTemplate.setTemplateName("CrossTabTemplate Test 1");
		CrossTabTemplateDetail templateDetail = new CrossTabTemplateDetail();
		templateDetail.setAttributeDisplayName("country");
		templateDetail.setAttributeDisplaySequence(1);
		templateDetail.setFieldType(CrossTabFieldType.COLUMN);
		templateDetail.setGroupOrAggregate(GroupOrAggregate.GROUPING);
		templateDetail.setSqlFunction(SqlFunction.GROUPBY);
		templateDetail.setSqltype(SqlTypeEnum.VARCHAR);
		templateDetail.setModelAttributeName("country");
		CrossTabTemplateDetail templateDetail1 = new CrossTabTemplateDetail();
		templateDetail1.setAttributeDisplayName("location");
		templateDetail1.setAttributeDisplaySequence(2);
		templateDetail1.setFieldType(CrossTabFieldType.COLUMN);
		templateDetail1.setGroupOrAggregate(GroupOrAggregate.GROUPING);
		templateDetail1.setSqlFunction(SqlFunction.GROUPBY);
		templateDetail1.setSqltype(SqlTypeEnum.VARCHAR);
		templateDetail1.setModelAttributeName("location");
		CrossTabTemplateDetail templateDetail2 = new CrossTabTemplateDetail();
		templateDetail2.setAttributeDisplayName("Number of Track");
		templateDetail2.setAttributeDisplaySequence(1);
		templateDetail2.setFieldType(CrossTabFieldType.COLUMN);
		templateDetail2.setGroupOrAggregate(GroupOrAggregate.AGGREGATE);
		templateDetail2.setSqlFunction(SqlFunction.COUNT);
		templateDetail2.setSqltype(SqlTypeEnum.VARCHAR);
		templateDetail2.setModelAttributeName("name");
		list.add(templateDetail2);
		list.add(templateDetail1);
		list.add(templateDetail);
		crossTabTemplate.setCrossTabDetail(list);
		/*try {
			BaseReportTemplate baseReportTemplate = reportTemplateDAO.insert(crossTabTemplate);
			LOG.info(String.valueOf(baseReportTemplate));
		} catch (ReportTemplateDAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		Optional<CrossTabTemplate> template = reportTemplateService.save(crossTabTemplate);
		LOG.info(String.valueOf(template));
		
	}
	
	@Test
	public void checkForNull(){
		//Optional<CrossTabTemplate> template = reportTemplateService.save(null);
		
	}

}
