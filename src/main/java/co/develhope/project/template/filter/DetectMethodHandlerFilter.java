package co.develhope.project.template.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.WebUtils;

import co.develhope.project.template.utils.Constants;

@Component
@Order(0)
public class DetectMethodHandlerFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(DetectMethodHandlerFilter.class);
	
	private HandlerMapping requestHandlerMapping;

	public DetectMethodHandlerFilter(ApplicationContext applicationContext) {
		this.requestHandlerMapping = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, HandlerMapping.class, true, false).get("requestMappingHandlerMapping");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String requestUri = (String) request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
			requestUri = (requestUri != null ? requestUri : request.getRequestURI());
			RequestPath requestPath = RequestPath.parse(requestUri, request.getContextPath());
			request.setAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE, requestPath);
			HandlerExecutionChain chain = requestHandlerMapping.getHandler(request);
			if(chain != null && chain.getHandler() != null 
					&& chain.getHandler() instanceof HandlerMethod) {
				logger.debug("Found valid HandlerExecutionChain");
				request.setAttribute(Constants.HANDLE_METHOD_FOR_AUTHORIZATION_ATTRIBUTE, (HandlerMethod)chain.getHandler());
				filterChain.doFilter(request, response);			
			} else {
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
		} catch (Exception e) {
			logger.error("Exception detecting method handler", e);
		}
	}

	
	
}
