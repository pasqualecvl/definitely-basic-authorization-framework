package co.develhope.project.template.service;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetails {

	private String username;

	private List<String> roles;

	@JsonIgnore
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
