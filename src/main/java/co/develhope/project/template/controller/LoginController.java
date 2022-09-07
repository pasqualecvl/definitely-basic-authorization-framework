package co.develhope.project.template.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.develhope.project.template.security.PublicEndpoint;
import co.develhope.project.template.service.UserService;
import co.develhope.project.template.utils.Constants;

@RestController
public class LoginController {

	@Autowired
	private UserService userService;

	@PublicEndpoint
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<Void> doLogin(@RequestParam MultiValueMap<String, String> params) {
		String jws = userService.checkUserCredentials(params.getFirst("username"), params.getFirst("password"));
		if (jws != null) {
			MultiValueMap<String, String> headers = new HttpHeaders();
			headers.put(Constants.X_AUTHENTICATION_HEADER, Arrays.asList( "Bearer " + jws));
			return new ResponseEntity<>(headers, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}
}
