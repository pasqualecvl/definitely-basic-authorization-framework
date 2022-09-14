package it.pasqualecavallo.studentsmaterial.authorization_framework.service;

public interface UserService {

	public UserDetails checkUserCredentials(String username, String password);
}
