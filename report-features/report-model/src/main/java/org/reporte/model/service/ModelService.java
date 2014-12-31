package org.reporte.model.service;

import java.util.List;

import javax.ejb.Local;

import org.reporte.model.domain.Model;
import org.reporte.model.service.exception.ModelServiceException;

@Local
public interface ModelService {
	/**
	 * 
	 * @param model
	 * @return
	 * @throws ModelServiceException if there is any problem during the process.
	 * @throws IllegalArgumentException if <code>model</code> is <code>null</code>.
	 */
	Model save(Model model) throws ModelServiceException;
	/**
	 * 
	 * @param model
	 * @throws ModelServiceException if there is any problem during the process.
	 * @throws IllegalArgumentException if <code>model</code> is <code>null</code>.
	 */
	void update(Model model) throws ModelServiceException;
	/**
	 * 
	 * @param model
	 * @throws ModelServiceException if there is any problem during the process.
	 * @throws IllegalArgumentException if <code>model</code> is <code>null</code>.
	 */
	void delete(Model model) throws ModelServiceException;
	/**
	 * 
	 * @param id
	 * @return
	 * @throws ModelServiceException
	 */
	Model find(int id) throws ModelServiceException;
	/**
	 * 
	 * @return
	 * @throws ModelServiceException
	 */
	List<Model> findAll() throws ModelServiceException;
	
	/**
	 * 
	 * @return
	 * @throws ModelServiceException
	 */
	List<Model> findAllOrderByDatasourceName() throws ModelServiceException;
	/**
	 * 
	 * @return
	 * @throws IllegalStateException if <code>DatasourceHandler</code> object reference couldn't be resolved when requested.
	 */
	DatasourceHandler getDatasourceHandler();
	/**
	 * 
	 * @return
	 * @throws IllegalStateException if <code>DatabaseTypeHandler</code> object reference couldn't be resolved when requested.
	 */
	DatabaseTypeHandler getDatabaseTypeHandler();
	/**
	 * 
	 * @return
	 * @throws IllegalStateException if <code>DatabaseTypeHandler</code> object reference couldn't be resolved when requested.
	 */
	JdbcClient getJdbcClient();
}
