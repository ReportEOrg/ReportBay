package org.reporte.reporttemplate.service.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.reporte.reporttemplate.dao.ReportTemplateDAO;
import org.reporte.reporttemplate.dao.exception.ReportTemplateDAOException;
import org.reporte.reporttemplate.domain.AreaChartTemplate;
import org.reporte.reporttemplate.domain.BarChartTemplate;
import org.reporte.reporttemplate.domain.BaseReportTemplate;
import org.reporte.reporttemplate.domain.ColumnChartTemplate;
import org.reporte.reporttemplate.domain.LineChartTemplate;
import org.reporte.reporttemplate.domain.PieChartTemplate;
import org.reporte.reporttemplate.service.ReportTemplateService;
import org.reporte.reporttemplate.service.exception.ReportTemplateServiceException;

//stateless session bean
@Stateless
//container managed transaction manager
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ReportTemplateServiceImpl implements ReportTemplateService{
	
	@Inject
	private ReportTemplateDAO reportTemplateDAO;
	
	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private BaseReportTemplate saveReportTemplate(BaseReportTemplate reportTemplate) throws ReportTemplateServiceException{
		try{
			return reportTemplateDAO.insert(reportTemplate);
		}
		catch(ReportTemplateDAOException rtde){
			throw new ReportTemplateServiceException("Failed saving report template "+reportTemplate.getTemplateName(),rtde);
		}
	}
	
	/**
	 * 
	 * @param reportTemplate
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private BaseReportTemplate updateReportTemplateWithEntityCacheSync(BaseReportTemplate reportTemplate) throws ReportTemplateServiceException{
		try{
			//as JPA merge will "copy" the state of detached object into the persistent entity and return the persistence entity reference, 
			//hibernate entity manager appear to unaware of the "detached" entity changes, if the same detached object is used subsequently, 
			//hibernate entity manager may incorrectly assume the cached "managed" entity still up to date and cause error at db level operation
			//e.g. "Detached" entity having its OneToMany attribute removed and merge(), followed by delete(), hibernate EM not aware that 
			//     the oneToMany attributes changes and attempt to perform remove operation on entity no longer exist
			
			
			return reportTemplateDAO.updateEntity(reportTemplate);
		}
		catch(ReportTemplateDAOException rtde){
			throw new ReportTemplateServiceException("Failed to update report template "+reportTemplate.getTemplateName(),rtde);
		}
	}
	
	/**
	 * 
	 * @param reportTemplate
	 * @throws ReportTemplateServiceException
	 */
	private void deleteReportTemplate(BaseReportTemplate reportTemplate) throws ReportTemplateServiceException{
		try{
			reportTemplateDAO.delete(reportTemplate);
		}
		catch(ReportTemplateDAOException rtde){
			throw new ReportTemplateServiceException("Failed to delete report template "+reportTemplate.getTemplateName(),rtde);
		}
	}
	/**
	 * 
	 * @param reportTemplateId
	 * @return
	 * @throws ReportTemplateServiceException
	 */
	private BaseReportTemplate findReportTemplate(int reportTemplateId) throws ReportTemplateServiceException{
		try{
			return reportTemplateDAO.find(reportTemplateId);
		}
		catch(ReportTemplateDAOException rtde){
			throw new ReportTemplateServiceException("Failed finding report template for id = "+reportTemplateId,rtde);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public AreaChartTemplate save(AreaChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		return (AreaChartTemplate)saveReportTemplate(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public BarChartTemplate save(BarChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		return (BarChartTemplate)saveReportTemplate(reportTemplate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ColumnChartTemplate save(ColumnChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		return (ColumnChartTemplate)saveReportTemplate(reportTemplate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public LineChartTemplate save(LineChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		return (LineChartTemplate)saveReportTemplate(reportTemplate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public PieChartTemplate save(PieChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		return (PieChartTemplate)saveReportTemplate(reportTemplate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public AreaChartTemplate update(AreaChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		return (AreaChartTemplate)updateReportTemplateWithEntityCacheSync(reportTemplate);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public AreaChartTemplate updateAreaChartTemplate(AreaChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		try{
			return (AreaChartTemplate)reportTemplateDAO.updateEntity(reportTemplate);
		}
		catch(ReportTemplateDAOException rtde){
			throw new ReportTemplateServiceException("err", rtde);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(AreaChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		deleteReportTemplate(reportTemplate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AreaChartTemplate findAreaChartTemplate(int reportTemplateId)
			throws ReportTemplateServiceException {
		return (AreaChartTemplate)findReportTemplate(reportTemplateId);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public BarChartTemplate update(BarChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		return (BarChartTemplate)updateReportTemplateWithEntityCacheSync(reportTemplate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(BarChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		deleteReportTemplate(reportTemplate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BarChartTemplate findBarChartTemplate(int reportTemplateId)
			throws ReportTemplateServiceException {
		return (BarChartTemplate)findReportTemplate(reportTemplateId);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ColumnChartTemplate update(ColumnChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		return (ColumnChartTemplate)updateReportTemplateWithEntityCacheSync(reportTemplate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(ColumnChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		deleteReportTemplate(reportTemplate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ColumnChartTemplate findColumnChartTemplate(int reportTemplateId)
			throws ReportTemplateServiceException {
		return (ColumnChartTemplate)findReportTemplate(reportTemplateId);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public LineChartTemplate update(LineChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		return (LineChartTemplate)updateReportTemplateWithEntityCacheSync(reportTemplate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(LineChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		deleteReportTemplate(reportTemplate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LineChartTemplate findLineChartTemplate(int reportTemplateId)
			throws ReportTemplateServiceException {
		return (LineChartTemplate)findReportTemplate(reportTemplateId);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public PieChartTemplate update(PieChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		return (PieChartTemplate)updateReportTemplateWithEntityCacheSync(reportTemplate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(PieChartTemplate reportTemplate)
			throws ReportTemplateServiceException {
		deleteReportTemplate(reportTemplate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PieChartTemplate findPieChartTemplate(int reportTemplateId)
			throws ReportTemplateServiceException {
		return (PieChartTemplate)findReportTemplate(reportTemplateId);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BaseReportTemplate> findAllReportTemplate()
			throws ReportTemplateServiceException {
		try{
			return reportTemplateDAO.findAll();
		}
		catch(ReportTemplateDAOException rtde){
			throw new ReportTemplateServiceException("find all report template failed", rtde);
		}
	}
}