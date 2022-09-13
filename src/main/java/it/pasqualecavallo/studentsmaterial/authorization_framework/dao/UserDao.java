package it.pasqualecavallo.studentsmaterial.authorization_framework.dao;

import it.pasqualecavallo.studentsmaterial.authorization_framework.service.UserDetails;

public interface UserDao {

	public UserDetails getUserByUsername(String username);
	
}
