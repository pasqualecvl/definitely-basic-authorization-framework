package co.develhope.project.template;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PublicEndpointTestIT {
	
	@MockBean
	private UserDao userDao;
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@BeforeEach
	public void setup() {
	}
	
	/**
	 * - No authentication
	 * - @PublicEndpoint on the handling method
	 */
	@Test
	public void testPublicEndpoint() {
		ResponseEntity<Void> response = restTemplate.getForEntity("http://localhost:"+port+"/public-endpoint", Void.class);
		assertTrue(response.getStatusCode().equals(HttpStatus.OK));
	}

	
}
