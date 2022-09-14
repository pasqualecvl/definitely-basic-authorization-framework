package it.pasqualecavallo.studentsmaterial.authorization_framework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import it.pasqualecavallo.studentsmaterial.authorization_framework.dao.UserDao;

@Service
public class DefaultUserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	public UserDetails checkUserCredentials(String username, String password) {
		if (userDao == null) {
			Assert.notNull(userDao, "userDao is null. Define a UserDao implementation as a Spring Bean");
		}
		return userDao.getUserByUsername(username);
	}
}
