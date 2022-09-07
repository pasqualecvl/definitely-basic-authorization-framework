package co.develhope.project.template.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;

import co.develhope.project.template.security.PublicEndpoint;
import co.develhope.project.template.security.RoleSecurity;
import co.develhope.project.template.security.ZeroSecurity;
import co.develhope.project.template.utils.Constants;

@Component
@Order(1)
public class MethodSecurityFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(MethodSecurityFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		boolean authorized = false;
		try {
			HandlerMethod method = (HandlerMethod) request
					.getAttribute(Constants.HANDLE_METHOD_FOR_AUTHORIZATION_ATTRIBUTE);

			if (method.hasMethodAnnotation(ZeroSecurity.class) || method.hasMethodAnnotation(PublicEndpoint.class)) {
				logger.debug("Found ZeroSecurity security config. Proceed");
				authorized = true;
				filterChain.doFilter(request, response);
			} else if (method.hasMethodAnnotation(RoleSecurity.class)) {
				logger.debug("Found RoleSecurity security config. Check roles");
				RoleSecurity rolesSecurity = method.getMethodAnnotation(RoleSecurity.class);
				if (rolesSecurity != null) {
					List<String> userRoles = AuthenticationContext.get().getRoles();
					logger.debug("Security Role: {}, User roles: {}", rolesSecurity, userRoles);
					if (userRoles != null) {
						for (String roleSecurity : rolesSecurity.value()) {
							if (userRoles.contains(roleSecurity)) {
								logger.info("Authorization granted for role {}", roleSecurity);
								authorized = true;
								filterChain.doFilter(request, response);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception evaluating method security", e);
		}

		if (!authorized) {
			response.sendError(403);
		}
	}

}
