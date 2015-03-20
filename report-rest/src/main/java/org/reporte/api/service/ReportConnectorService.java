package org.reporte.api.service;

import org.reporte.api.dto.report.RestReport;
import org.reporte.api.dto.reportconnector.RestReportConnector;
import org.reporte.api.dto.reportconnector.RestReportConnectors;
import org.reporte.api.service.exception.ReportConnectorServiceException;

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
}