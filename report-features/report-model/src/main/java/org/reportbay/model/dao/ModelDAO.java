package org.reportbay.model.dao;

import java.util.List;

import org.reportbay.common.dao.BaseDAO;
import org.reportbay.model.dao.exception.ModelDAOException;
import org.reportbay.model.domain.Model;

public interface ModelDAO extends BaseDAO<Model, ModelDAOException> {
	
	/**
	 * 
	 * @return
	 * @throws ModelDAOException
	 */
	List<Model> findAllOrderByDatasourceName() throws ModelDAOException;
}
