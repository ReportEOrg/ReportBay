package org.reporte.model.dao;

import java.util.List;

import org.reporte.model.dao.exception.ModelDAOException;
import org.reporte.model.domain.Model;

public interface ModelDAO {
	/**
	 * 
	 * @param model
	 * @return
	 * @throws ModelDAOException
	 */
	Model insert(Model model) throws ModelDAOException;
	/**
	 * 
	 * @param model
	 * @throws ModelDAOException
	 */
	void update(Model model) throws ModelDAOException;
	/**
	 * 
	 * @param model
	 * @throws ModelDAOException
	 */
	void delete(Model model) throws ModelDAOException;
	/**
	 * 
	 * @param id
	 * @return
	 * @throws ModelDAOException
	 */
	Model find(int id) throws ModelDAOException;
	/**
	 * 
	 * @return
	 * @throws ModelDAOException
	 */
	List<Model> findAll() throws ModelDAOException;
	/**
	 * 
	 * @return
	 * @throws ModelDAOException
	 */
	List<Model> findAllOrderByDatasourceName() throws ModelDAOException;
}
