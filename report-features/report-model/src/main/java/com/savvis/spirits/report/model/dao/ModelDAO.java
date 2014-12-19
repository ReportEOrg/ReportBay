package com.savvis.spirits.report.model.dao;

import java.util.List;

import com.savvis.spirits.report.model.domain.Model;

public interface ModelDAO {
	public Model insert(Model model) throws ModelDAOException;
	public void update(Model model) throws ModelDAOException;
	public void delete(Model model) throws ModelDAOException;
	public Model find(int id) throws ModelDAOException;
	public List<Model> findAll() throws ModelDAOException;
}
