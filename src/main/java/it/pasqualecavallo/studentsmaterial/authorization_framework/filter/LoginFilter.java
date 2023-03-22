package it.pasqualecavallo.studentsmaterial.authorization_framework.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import it.pasqualecavallo.studentsmaterial.authorization_framework.service.UserDetails;
import it.pasqualecavallo.studentsmaterial.authorization_framework.service.UserService;
import it.pasqualecavallo.studentsmaterial.authorization_framework.utils.BCryptPasswordEncoder;
import it.pasqualecavallo.studentsmaterial.authorization_framework.utils.Constants;
import it.pasqualecavallo.studentsmaterial.authorization_framework.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(0)
public class LoginFilter extends OncePerRequestFilter {

	@Autowired
	private UserService userService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtils jwtUtils;

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
		UserDetails userDetails = userService.checkUserCredentials(params.get("username")[0],
				params.get("password")[0]);
		if (userDetails != null && passwordEncoder.matches(params.get("password")[0], userDetails.getPassword())) {
			String jws = "Bearer " + jwtUtils.getJws(userDetails.getUsername(), userDetails.getUserId(), userDetails.getRoles());
			if (jws != null) {
				response.setHeader(Constants.X_AUTHENTICATION_HEADER, jws);
			} else {
				response.sendError(HttpStatus.UNAUTHORIZED.value());
			}
		} else {
			response.sendError(HttpStatus.UNAUTHORIZED.value());
		}
	}

}
