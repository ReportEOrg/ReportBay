package org.reportbay.api.service;

import org.reportbay.api.dto.report.RestReport;
import org.reportbay.api.dto.report.RestReports;
import org.reportbay.api.dto.reportconnector.RestReportConnector;
import org.reportbay.api.dto.reportconnector.RestReportConnectors;
import org.reportbay.api.service.exception.ReportConnectorServiceException;

public interface ReportConnectorService{

	/**
	 * 
	 * @return
	 * @throws ReportConnectorServiceException
	 */
	RestReportConnectors findAllReportConnectors() throws ReportConnectorServiceException;
	
	/**
	 * 
	 * @param reportConnectorId
	 * @return
	 * @throws ReportConnectorServiceException
	 */
	RestReportConnector find(int reportConnectorId) throws ReportConnectorServiceException;
	
	/**
	 * 
	 * @param restReportConnector
	 * @return
	 * @throws ReportConnectorServiceException
	 */
	RestReport generateReportPreview(RestReportConnector restReportConnector) throws ReportConnectorServiceException;
	
	/**
	 * 
	 * @param reportConnectorId
	 * @return
	 * @throws ReportConnectorServiceException
	 */
	RestReport generateReportPreview(int reportConnectorId) throws ReportConnectorServiceException;
	
	/**
	 * 
	 * @param restReportConnector
	 * @return
	 * @throws ReportConnectorServiceException
	 */
	RestReportConnector save(RestReportConnector restReportConnector) throws ReportConnectorServiceException;
	
	/**
	 * 
	 * @param reportConnectorId
	 * @throws ReportConnectorServiceException
	 */
	void delete(int reportConnectorId) throws ReportConnectorServiceException;
	
	/**
	 * 
	 * @param restReportConnector
	 * @return
	 * @throws ReportConnectorServiceException
	 */
	RestReportConnector update(RestReportConnector restReportConnector) throws ReportConnectorServiceException;
	
	/**
	 * 
	 * @param reportConnectorId
	 * @return
	 * @throws ReportConnectorServiceException
	 */
	RestReport generateReportSnapshot(int reportConnectorId) throws ReportConnectorServiceException;
	
	/**
	 * 
	 * @param reportId
	 * @return
	 * @throws ReportConnectorServiceException
	 */
	RestReport getReportSnapshot(int reportId) throws ReportConnectorServiceException;
	
	/**
	 * 
	 * @return
	 * @throws ReportConnectorServiceException
	 */
	RestReports getReports() throws ReportConnectorServiceException;
}