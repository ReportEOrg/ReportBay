package org.reportbay.api.rest;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ResponseInterceptor implements WriterInterceptor{

	private static final Logger LOG = LoggerFactory.getLogger(ResponseInterceptor.class);
	
	@Override
	public void aroundWriteTo(WriterInterceptorContext context)
			throws IOException, WebApplicationException {
		MultivaluedMap<String, Object> headers = context.getHeaders();
		
		//TODO: put in proper domain
		
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");	
		
		LOG.debug("setting response header: {}",headers.toString());
		
	}
	
}