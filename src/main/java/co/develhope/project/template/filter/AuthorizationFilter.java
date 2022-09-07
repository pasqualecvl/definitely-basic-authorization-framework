package co.develhope.project.template.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;

import co.develhope.project.template.filter.AuthenticationContext.Principal;
import co.develhope.project.template.security.PublicEndpoint;
import co.develhope.project.template.utils.Constants;
import co.develhope.project.template.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@Component
@Order(1)
public class AuthorizationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			HandlerMethod handlerMethod = (HandlerMethod) request
					.getAttribute(Constants.HANDLE_METHOD_FOR_AUTHORIZATION_ATTRIBUTE);
			String authenticationHeader = request.getHeader(Constants.X_AUTHENTICATION_HEADER);

			if (authenticationHeader == null && handlerMethod.hasMethodAnnotation(PublicEndpoint.class)) {
				filterChain.doFilter(request, response);
			} else if (authenticationHeader != null) {
				String[] tokens = authenticationHeader.split(" ");
				if (tokens[0].equals("Bearer")) {
					Jws<Claims> claims = jwtUtils.decodeJwt(tokens[1]);
					Principal principal = new Principal(claims.getBody().getSubject(),
							(List<String>) claims.getBody().get(Constants.CLAIM_USER_ROLES, List.class));
					AuthenticationContext.set(principal);
					filterChain.doFilter(request, response);
				}
			} else {
				response.sendError(HttpStatus.FORBIDDEN.value());
			}
		} finally {
			AuthenticationContext.remove();
		}

	}

}
