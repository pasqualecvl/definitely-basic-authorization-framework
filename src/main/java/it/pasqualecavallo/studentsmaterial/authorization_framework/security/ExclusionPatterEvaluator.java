package it.pasqualecavallo.studentsmaterial.authorization_framework.security;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.WebUtils;

import it.pasqualecavallo.studentsmaterial.authorization_framework.utils.Constants;

public class ExclusionPatterEvaluator {

	private List<String> antPaths;
	
	private PathMatcher pathMatcher = new AntPathMatcher();
	
	public boolean evaluateExclusion(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		for (String path : antPaths) {
			if (pathMatcher.match(path, requestUri)) {
				request.setAttribute(Constants.SKIP_AUTHORIZATION_FILTERCHAIN_ATTRIBUTE, Boolean.TRUE);
				return true;
			}
		}
		request.setAttribute(Constants.SKIP_AUTHORIZATION_FILTERCHAIN_ATTRIBUTE, Boolean.FALSE);
		return false;

	}
	
	public ExclusionPatterEvaluator mustExcludeAntPathMatchers(String...antPath) {
		this.antPaths = Arrays.asList(antPath);
		return this;
	}
	
}
