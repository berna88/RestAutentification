package com.consistent.autentification.application;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;

import com.consistent.autentification.segurity.Autentification;
import com.consistent.autentification.segurity.AutentificationImp;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;


/**
 * @author liferay
 */
@ApplicationPath("/greetings")

@Component(immediate = true, service = Application.class)
public class ServiceAutentificationApplication extends Application{
	
	private static final Log log = LogFactoryUtil.getLog(ServiceAutentificationApplication.class);

	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	@GET
	@Produces("text/plain")
	public String working() {
		return "It works!";
	}

	@GET
	@Path("/morning")
	@Produces("text/plain")
	public String hello(@Context HttpServletRequest request, 
			@Context HttpHeaders headers) throws UnsupportedEncodingException, PortalException {
		String status = "";
		Autentification autentification = new AutentificationImp(request, headers);
		if(autentification.isAutentificationBasic()){
			log.info("paso");
			status = "paso";
		}else {
			log.error("Problema con las credenciales del usuario");
			status = String.valueOf(Response.status(401).build().getStatus());
		}
		
		return status;
		
	}

	@GET
	@Path("/morning/{groupId}")
	@Produces("text/plain")
	public String morning(
		@PathParam("groupId") long groupId,
		@QueryParam("drink") String drink) throws PortalException {
		Long companyId = CompanyThreadLocal.getCompanyId();
		User user = UserLocalServiceUtil.getUserByEmailAddress(companyId, "test@liferay.com");
		
		log.info(user);
		user.setPasswordEncrypted(false);
		log.info(user);
		//log.info(msg);
		String greeting = "Good Morning " + groupId;

		if (drink != null) {
			greeting += ". Would you like some " + drink + "?";
		}

		return greeting;
	}
/*	
	
	public Response getValidate(
			@Context HttpServletRequest request, 
			@Context HttpHeaders headers){
		
	try {
		String token = Optional.ofNullable(headers.getRequestHeader(HttpHeaders.AUTHORIZATION)).orElse(Collections.<String> emptyList()).stream().collect(Collectors.joining(": "));
		 if (token == null || !token.startsWith("Basic")) {
		        return Response.status(Response.Status.UNAUTHORIZED).entity("La autenticación no es soportada").build();
		    }
		 log.info("token: "+token);
		 
		 String[] tokens = (new String(Base64.getDecoder().decode(token.split(" ")[1]), "UTF-8")).split(":");
		 
		 log.info(tokens);
		 
		 if (tokens == null) {
			 log.info(Response.status(Response.Status.UNAUTHORIZED).entity("El mecanismo de autenticación ha fallado").build());
		       return Response.status(Response.Status.UNAUTHORIZED).entity("El mecanismo de autenticación ha fallado").build();
		 }
		 log.info(tokens[0]);
		 log.info(UserLocalServiceUtil.getUserByEmailAddress(PortalUtil.getCompanyId(request), tokens[0]).getAddresses());
		 User user = UserLocalServiceUtil.getUserByEmailAddress(PortalUtil.getCompanyId(request), tokens[0]);
		 
		 log.info("user: "+user.getEmailAddress());
		 
		 boolean validUser = PasswordTrackerLocalServiceUtil.isSameAsCurrentPassword(user.getUserId(), tokens[1]);
		 log.info("validUser: "+validUser);
		 if(validUser){
			 return Response.ok(MediaType.APPLICATION_JSON).build();
		 }else{
			 return Response.status(401).build();
		 }
		 
	} catch (Exception e) {
		// TODO: handle exception
	}
		
				return null;
		
	}


*/
	
	
	

}