package org.reportbay.api.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@PreMatching
public class RequestFilter implements ContainerRequestFilter {

	private final static Logger LOG = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    public void filter( ContainerRequestContext requestCtx ) throws IOException {
        LOG.debug( "Executing REST request filter for method ["+requestCtx.getRequest().getMethod()+"]" );
        // When HttpMethod comes as OPTIONS, just acknowledge that it accepts...
        if ( requestCtx.getRequest().getMethod().equals( "OPTIONS" ) ) {
            LOG.info( "HTTP Method (OPTIONS) - Detected! Aborting Request with Status 200" );
            // Just send a OK signal back to the browser
            //requestCtx.abortWith( Response.status( Response.Status.OK ).build() );
        }
    }

}
