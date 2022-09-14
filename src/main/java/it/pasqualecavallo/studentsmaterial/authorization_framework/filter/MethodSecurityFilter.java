package it.pasqualecavallo.studentsmaterial.authorization_framework.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;

import it.pasqualecavallo.studentsmaterial.authorization_framework.security.HierarchicalSecurity;
import it.pasqualecavallo.studentsmaterial.authorization_framework.security.PublicEndpoint;
import it.pasqualecavallo.studentsmaterial.authorization_framework.security.RoleSecurity;
import it.pasqualecavallo.studentsmaterial.authorization_framework.security.RoleVisitor;
import it.pasqualecavallo.studentsmaterial.authorization_framework.security.ZeroSecurity;
import it.pasqualecavallo.studentsmaterial.authorization_framework.utils.Constants;

@Component
@Order(3)
public class MethodSecurityFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(MethodSecurityFilter.class);

	@Autowired(required = false)
	private RoleVisitor roleVisitor;
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return Boolean.TRUE.equals(((Boolean)request.getAttribute(Constants.SKIP_AUTHORIZATION_FILTERCHAIN_ATTRIBUTE)));
	}

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
			} else if (method.hasMethodAnnotation(HierarchicalSecurity.class)) {
				Assert.notNull(roleVisitor, "A RoleVisitor implementation must be defined a spring bean");
				HierarchicalSecurity hierarchicalSecurity = method.getMethodAnnotation(HierarchicalSecurity.class);
				List<String> userRoles = AuthenticationContext.get().getRoles();
				logger.debug("Security Role: {}, User roles: {}", hierarchicalSecurity.bottomRole(), userRoles);
				if (hierarchicalSecurity != null && roleVisitor
						.isRoleHierarchicallyUpperOrEqualsTo(hierarchicalSecurity.bottomRole(), userRoles)) {
					logger.info("Authorization granted for role {}", hierarchicalSecurity.bottomRole());
					authorized = true;
					filterChain.doFilter(request, response);
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
