package org.reportbay.publish.service;

import java.io.Serializable;
import java.util.Properties;

import org.reportbay.publish.service.exception.PublishServiceException;


public interface PublishService extends Serializable{
	
	/**
	 * 
	 * @return
	 */
	String getServiceName();

	/**
	 * 
	 * @param publishName
	 * @param parameters
	 * @throws PublishServiceException
	 */
	void publish(String publishName, Properties parameters) throws PublishServiceException;
	
	/**
	 * 
	 * @param prop
	 * @param profile //TODO: change to proper class
	 */
	void prepareParamFromProfile(Properties prop, Object profile);
}