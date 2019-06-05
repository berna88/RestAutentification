package com.consistent.autentification.application;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.AutoLoginException;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;


/**
 * @author liferay
 */
@ApplicationPath("/greetings")
@Component(immediate = true, service = Application.class)
public class ServiceAutentificationApplication extends Application implements ContainerRequestFilter,AutoLogin {
	
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
	public String hello() {
		return "Good morning!";
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

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
				String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
				
				log.info(authorizationHeader);
				
				if(authorizationHeader == null || !authorizationHeader.startsWith("Basic")){
					throw new NotAuthorizedException("Debe tener tu autorizacion");
				}
				
				String token = authorizationHeader.substring("Basic".length()).trim();
				log.info(token);
				requestContext.getRequest();
				String userId = requestContext.getUriInfo().getPathParameters().getFirst("id");
				log.info(userId);
				Long companyId = CompanyThreadLocal.getCompanyId();
				
				try {
					User user = UserLocalServiceUtil.getUserByEmailAddress(companyId, "test@liferay.com");
					log.info("isPasswordEncrypted"+user.isPasswordEncrypted());
					log.info("isActive"+user.isActive());
					log.info("login"+user.getLogin());
					log.info("isLockout"+user.isLockout());
					log.info(user.getPasswordUnencrypted());
				} catch (PortalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
	}

	@Override
	public String[] handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
			throws AutoLoginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] login(HttpServletRequest request, HttpServletResponse response) throws AutoLoginException {
		try {
			Long companyId = CompanyThreadLocal.getCompanyId();
			User user = UserLocalServiceUtil.getUserByEmailAddress(companyId, "test@liferay");
			System.out.println(user);
			log.info(new String[]{String.valueOf(user.getUserId()), user.getPassword(), String.valueOf(user.isPasswordEncrypted()) });
			return new String[]{String.valueOf(user.getUserId()), user.getPassword(), String.valueOf(user.isPasswordEncrypted()) };
			} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
			}
	}
	
	

}