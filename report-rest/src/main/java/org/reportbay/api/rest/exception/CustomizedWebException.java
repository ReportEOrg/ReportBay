package org.reportbay.api.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 
 * Customized web exception
 *
 */
public class CustomizedWebException extends WebApplicationException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public CustomizedWebException(){
	}

	/**
	 * 
	 * @param status
	 * @param message
	 */
	public CustomizedWebException(Response.Status status, String message){
		super(Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build());
	}
	
	/**
	 * 
	 * @param status
	 * @param throwable
	 */
	public CustomizedWebException(Response.Status status, Throwable throwable){
		super(throwable, status);
	}
}