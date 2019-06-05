package com.consistent.autentification.segurity;

import java.io.IOException;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;


public class AutentificationFilter implements ContainerRequestFilter{

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// TODO Auto-generated method stub
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer")){
			throw new NotAuthorizedException("Debe tener tu autorizacion");
		}
		
		String token = authorizationHeader.substring("Bearer".length()).trim();
		
		String userId = requestContext.getUriInfo().getPathParameters().getFirst("id");
		validateToken(token, userId);
		
	}
	
	private void validateToken(String token, String userId){
		
	}

}
