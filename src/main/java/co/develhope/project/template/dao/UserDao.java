package co.develhope.project.template.dao;

import co.develhope.project.template.service.UserDetails;

public interface UserDao {

	public UserDetails getUserByUsername(String username);
	
}
