package co.develhope.project.template.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import co.develhope.project.template.dao.UserDao;
import co.develhope.project.template.utils.BCryptPasswordEncoder;
import co.develhope.project.template.utils.JwtUtils;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtils jwtUtils;

	public String checkUserCredentials(String username, String password) {
		if (userDao == null) {
			Assert.notNull(userDao, "userDao is null. Define a UserDao implementation as a Spring Bean");
		}
		UserDetails userDetails = userDao.getUserByUsername(username);
		if (userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
			return jwtUtils.getJws(userDetails.getUsername(), userDetails.getRoles());
		}
		return null;
	}
}
