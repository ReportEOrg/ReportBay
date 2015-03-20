package org.report.template.integration;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.reporte.model.domain.Model;
import org.reporte.reporttemplate.domain.CrossTabTemplate;
import org.reporte.reporttemplate.domain.CrossTabTemplateDetail;
import org.reporte.reporttemplate.domain.ReportQuery;
import org.reporte.reporttemplate.service.exception.ReportTemplateServiceException;
import org.reporte.reporttemplate.service.impl.CrossTabDetailsComparator;
import org.reporte.reporttemplate.service.impl.ReportTemplateServiceImpl;
import org.reporte.reporttemplate.visitor.SelectItemVisitorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
	private static final Logger LOG = LoggerFactory.getLogger(Test.class);

	public static void main(String[] args) {

		String query2 = "SELECT avg(c.city) as avg,max(c.city) as max,count(c.city) as count,first(c.city) as first,"
				+ "last(c.city) as last,min(c.city) as min,sum(c.city) as sum FROM Customers as c";
		String query1 = "SELECT UCASE(c.city) as UCASE,LCASE(c.city) as LCASE,MID(c.city) as MID,LEN(c.city) as LEN,"
				+ "ROUND(c.city) as ROUND,NOW(c.city) as NOW,FORMAT(c.city) as FORMAT FROM Customers as c";
		String query = "SELECT Shippers.ShipperName,COUNT(Orders.OrderID) AS NumberOfOrders FROM Orders "
						+"LEFT JOIN Shippers ON Orders.ShipperID=Shippers.ShipperID GROUP BY ShipperName,NumberOfOrders";
		String query3 = "SELECT DISTINCT(c.name) FROM Customer c";
		CCJSqlParserManager sqlParser = new CCJSqlParserManager();
		try {
			Statement statement = sqlParser.parse(new StringReader(query));
			if (statement instanceof Select) {
				Select selectStatement = (Select) statement;
				SelectBody selectBody = selectStatement.getSelectBody();
				if (selectBody!=null) {
					SelectItemVisitorImpl selectItemVisitorImpl = new SelectItemVisitorImpl();
					if (selectBody instanceof PlainSelect) {
						LOG.info("SelectBody of Type PlainSelect ["+selectBody+"]");
						PlainSelect plainSelect = (PlainSelect) selectBody;
						Distinct distinct = plainSelect.getDistinct();
						//If distinct object is not null, there is distinct clause in the select query
						if (distinct!=null) {
							List<SelectItem> distinctSelectItems = distinct.getOnSelectItems();
							if (CollectionUtils.isNotEmpty(distinctSelectItems)) {
								for (SelectItem selectItem : distinctSelectItems) {
									LOG.info("Select Item on Distinct ["+selectItem+"]");
									selectItem.accept(selectItemVisitorImpl);
								}
							}else{
								LOG.error("List of DistinctSelectItems is empty for the query ["+query+"]");
							}
						}
						ReportTemplateServiceImpl reportService = new ReportTemplateServiceImpl();
						String deparsedQuery = reportService.deparseSelect(plainSelect);
						List<SelectItem> selectItems = plainSelect.getSelectItems();
						if(CollectionUtils.isNotEmpty(selectItems)){
							//Loop through each Select Item to construct the list of Select fields
							for (SelectItem selectItem : selectItems) {
								LOG.info("Select Field ["+selectItem+"]");
								selectItem.accept(selectItemVisitorImpl);
							}
						}else{
							LOG.error("SelectItems list is empty for the query ["+query+"]");
						}
					}else if(selectBody instanceof SetOperationList){
						LOG.info("SelectBody of Type SetOperationList ["+selectBody+"]");
						SetOperationList setOperation = (SetOperationList) selectBody;
					}else if (selectBody instanceof WithItem) {
						LOG.info("SelectBody of Type WithItem ["+selectBody+"]");
						WithItem withItem = (WithItem) selectBody;
					}
				}else{
					LOG.error("SelectBody object is empty for the query ["+query+"]");
				}
			}
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private ReportQuery constructSelectAndGroupByCriteria(CrossTabTemplate crossTabTemplate, Model model) throws ReportTemplateServiceException{
		List<CrossTabTemplateDetail> crossTabDetails = crossTabTemplate.getCrossTabDetail();
		if(CollectionUtils.isNotEmpty(crossTabDetails)){
			//Sort the given CrossTabTemplate Detail according to customer request
			//This will sort the groupby fields accordingly
			Collections.sort(crossTabDetails, new CrossTabDetailsComparator());
			if(model.getQuery()!=null){
				//Check if the Query String in not null or empty
				if(StringUtils.isNoneBlank(model.getQuery().getValue())){
					String query = model.getQuery().getValue();
					LOG.info("Model Query ["+query+"]");
					//Parse the query using jsqlParser
					CCJSqlParserManager sqlParser = new CCJSqlParserManager();
					try {
						Statement statement = sqlParser.parse(new StringReader(query));
						if (statement instanceof Select) {
							Select selectStatement = (Select) statement;
							SelectBody selectBody = selectStatement.getSelectBody();
							if (selectBody!=null) {
								SelectItemVisitorImpl selectItemVisitorImpl = new SelectItemVisitorImpl();
								if (selectBody instanceof PlainSelect) {
									LOG.info("SelectBody of Type PlainSelect ["+selectBody+"]");
									PlainSelect plainSelect = (PlainSelect) selectBody;
									Distinct distinct = plainSelect.getDistinct();
									//If distinct object is not null, there is distinct clause in the select query
									if (distinct!=null) {
										List<SelectItem> distinctSelectItems = distinct.getOnSelectItems();
										if (CollectionUtils.isNotEmpty(distinctSelectItems)) {
											for (SelectItem selectItem : distinctSelectItems) {
												LOG.info("Select Item on Distinct ["+selectItem+"]");
												selectItem.accept(selectItemVisitorImpl);
											}
										}else{
											LOG.error("List of DistinctSelectItems is empty for the query ["+query+"]");
										}
									}
									List<SelectItem> selectItems = plainSelect.getSelectItems();
									if(CollectionUtils.isNotEmpty(selectItems)){
										//Loop through each Select Item to construct the list of Select fields
										for (SelectItem selectItem : selectItems) {
											LOG.info("Select Field ["+selectItem+"]");
											selectItem.accept(selectItemVisitorImpl);
										}
									}else{
										LOG.error("SelectItems list is empty for the query ["+query+"]");
									}
								}else if(selectBody instanceof SetOperationList){
									LOG.info("SelectBody of Type SetOperationList ["+selectBody+"]");
									SetOperationList setOperation = (SetOperationList) selectBody;
								}else if (selectBody instanceof WithItem) {
									LOG.info("SelectBody of Type WithItem ["+selectBody+"]");
									WithItem withItem = (WithItem) selectBody;
								}
							}else{
								LOG.error("SelectBody object is empty for the query ["+query+"]");
							}
						}
					} catch (JSQLParserException e) {
						throw new ReportTemplateServiceException("Error parsing SQL query from Model Id " + model.getId(), e);
					}
				}else{
					throw new ReportTemplateServiceException("Model Query String cannot be null/empty/blank for the ModelQuery Id"+model.getQuery().getId());
				}
			}else{
				throw new ReportTemplateServiceException("Model Query object is Null for the Model Id "+ model.getId());
			}
		}
		return null;
	}
}
