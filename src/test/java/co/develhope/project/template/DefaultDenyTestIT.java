package co.develhope.project.template;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import co.develhope.project.template.dao.UserDao;
import co.develhope.project.template.service.UserDetails;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DefaultDenyTestIT {

	@MockBean
	private UserDao userDao;
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@BeforeEach
	public void setup() {
		UserDetails userDetails = new UserDetails();
		when(userDao.getUserByUsername(any())).thenReturn(userDetails);
	}
	
	/**
	 * - No authentication
	 * - No @PublicEndpoint on the handling method
	 */
	@Test
	public void testDefaultDeny() {
		ResponseEntity<Void> response = restTemplate.getForEntity("http://localhost:"+port+"/default-deny", Void.class);
		assertTrue(response.getStatusCode().equals(HttpStatus.FORBIDDEN));
	}
	
}
