package co.develhope.project.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import co.develhope.project.template.dao.UserDao;
import co.develhope.project.template.security.RoleVisitor;
import co.develhope.project.template.service.UserDetails;
import co.develhope.project.template.utils.BCryptPasswordEncoder;
import co.develhope.project.template.utils.Constants;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HierarchicalSecurityTestIT {

	@MockBean
	private UserDao userDao;

	@MockBean
	private BCryptPasswordEncoder passwordEncoder;

	@MockBean
	private RoleVisitor roleVisitor;
	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@BeforeEach
	public void setup() {
		UserDetails noRoleUser = new UserDetails();
		noRoleUser.setPassword("password");
		noRoleUser.setRoles(java.util.Arrays.asList("NOT_SUFFICIENT_ROLE"));
		noRoleUser.setUsername("pasquale");
		when(userDao.getUserByUsername("noRoleUser")).thenReturn(noRoleUser);
		UserDetails roleUser = new UserDetails();
		roleUser.setPassword("password");
		roleUser.setRoles(java.util.Arrays.asList("SUFFICIENT_ROLE"));
		roleUser.setUsername("pasquale");
		when(userDao.getUserByUsername("roleUser")).thenReturn(roleUser);
		when(passwordEncoder.matches(any(), any())).thenReturn(true);
	}
	
	@Test
	public void testSufficientRoleAsHierarchy() throws URISyntaxException {
		when(roleVisitor.isRoleHierarchicallyUpperOrEqualsTo(Mockito.matches("SUFFICIENT_ROLE"), any())).thenReturn(true);
		String jws = doLogin("roleUser");
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constants.X_AUTHENTICATION_HEADER, jws);
		RequestEntity<Void> request = new RequestEntity<Void>(headers, HttpMethod.GET, new URI("http://localhost:"+port+"/hierarchi-evaluated-endpoint"));
		ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
		assertTrue(response.getStatusCode().equals(HttpStatus.OK));
	}

	@Test
	public void testNotSufficientRoleAsHierarchy() throws URISyntaxException {
		when(roleVisitor.isRoleHierarchicallyUpperOrEqualsTo(Mockito.matches("SUFFICIENT_ROLE"), any())).thenReturn(false);
		String jws = doLogin("noRoleUser");
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constants.X_AUTHENTICATION_HEADER, jws);
		RequestEntity<Void> request = new RequestEntity<Void>(headers, HttpMethod.GET, new URI("http://localhost:"+port+"/hierarchi-evaluated-endpoint"));
		ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
		assertTrue(response.getStatusCode().equals(HttpStatus.FORBIDDEN));
	}


	private String doLogin(String username) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("username", username);
		map.add("password", "pasquale");
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:" + port + "/login", request,
				Void.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		return response.getHeaders().get(Constants.X_AUTHENTICATION_HEADER).get(0);
	}
}
