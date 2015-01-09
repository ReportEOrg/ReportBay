package org.reporte.model.dao;

import java.util.List;

import org.reporte.common.dao.BaseDAO;
import org.reporte.model.dao.exception.ModelDAOException;
import org.reporte.model.domain.Model;

public interface ModelDAO extends BaseDAO<Model, ModelDAOException> {
	
	/**
	 * 
	 * @return
	 * @throws ModelDAOException
	 */
	List<Model> findAllOrderByDatasourceName() throws ModelDAOException;
}
