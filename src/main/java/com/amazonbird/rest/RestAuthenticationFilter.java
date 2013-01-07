package com.amazonbird.rest;

import javax.ws.rs.WebApplicationException;

import javax.ws.rs.core.Response;



import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;


public class RestAuthenticationFilter implements ContainerRequestFilter {

    public ContainerRequest filter(ContainerRequest request) {

    	String authHeader = "valid";
        if (authHeader != null) {

        	return request;
        }
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
}
