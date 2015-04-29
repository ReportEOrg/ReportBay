package org.reportbay.common.dao;

import java.util.List;

/**
 * 
 * base DAO interface
 *
 * @param <T1>
 * @param <T2>
 */
public interface BaseDAO<T1, T2 extends Throwable>{
	
	/**
	 * create entity
	 * 
	 * @param entity
	 * @return
	 * @throws T2
	 */
	T1 insert(T1 entity) throws T2;
	
	/**
	 * update entity
	 * 
	 * @param entity
	 * @throws T2
	 */
	void update(T1 entity) throws T2;
	/**
	 * delete entity
	 * @param entity
	 * @throws T2
	 */
	void delete(T1 entity) throws T2;
	/**
	 * find entity by primary key
	 * @param id
	 * @return
	 * @throws T2
	 */
	T1 find(int id) throws T2;
	/**
	 * find all entities
	 * @return
	 * @throws T2
	 */
	List<T1> findAll() throws T2;
}