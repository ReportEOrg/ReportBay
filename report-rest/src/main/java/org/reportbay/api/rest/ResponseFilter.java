package org.reportbay.api.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ResponseFilter implements ContainerResponseFilter{

	private static final Logger LOG = LoggerFactory.getLogger(ResponseFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		
		MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		
		//TODO: put in proper domain
		//1. supported origin site
		headers.add("Access-Control-Allow-Origin", "*");
		//2. supported methods
		headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
		//3. supported headers
		headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
		
		
		LOG.debug("setting response header: {}",headers.toString());
	}
	
}