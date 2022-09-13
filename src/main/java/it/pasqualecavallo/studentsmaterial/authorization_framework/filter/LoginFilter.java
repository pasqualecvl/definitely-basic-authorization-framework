package it.pasqualecavallo.studentsmaterial.authorization_framework.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import it.pasqualecavallo.studentsmaterial.authorization_framework.service.UserService;
import it.pasqualecavallo.studentsmaterial.authorization_framework.utils.Constants;

@Component
@Order(0)
public class LoginFilter extends OncePerRequestFilter {

	@Autowired
	private UserService userService;
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return !"/login".equals(request.getRequestURI());
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
	    Map<String, String[]> params = request.getParameterMap();
	    Assert.notEmpty(params.get("username"), "Username required");
	    Assert.notEmpty(params.get("password"), "Password required");
		String jws = userService.checkUserCredentials(params.get("username")[0], params.get("password")[0]);
		if (jws != null) {
			response.setHeader(Constants.X_AUTHENTICATION_HEADER, jws);
		} else {
			response.sendError(HttpStatus.UNAUTHORIZED.value());
		}
	}

}
