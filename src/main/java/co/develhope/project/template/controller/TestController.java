package co.develhope.project.template.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import co.develhope.project.template.filter.AuthenticationContext;
import co.develhope.project.template.security.PublicEndpoint;
import co.develhope.project.template.security.RoleSecurity;
import co.develhope.project.template.security.ZeroSecurity;

@RestController
public class TestController {

	@GetMapping("/default-deny")
	public void defaultDenyEndpoint() {
		System.out.println("Default deny. This endpoint cannot be reached");
	}

	@PublicEndpoint
	@GetMapping("/public-endpoint")
	public void publicEndpoint() {
		System.out.println("This endpoint can be reached without authentication header");
	}

	@ZeroSecurity
	@GetMapping("/no-role-endpoint")
	public void noRoleEndpoint() {
		System.out.println("This endpoint can be reached with any role, but require authentication. Authenticated user is " + AuthenticationContext.get().getUsername());
	}

	@RoleSecurity(value = { "ROLE_ADMIN", "ROLE_PUBLISHER" })
	@GetMapping("/role-evaluated-endpoint")
	public void roleCheckEndpoint() {
		System.out.println("This endpoint can be reached only by authenticated users with ROLE_ADMIN or ROLE_PUBLISHER. Authenticated user is " + AuthenticationContext.get().getUsername());
	}

}
