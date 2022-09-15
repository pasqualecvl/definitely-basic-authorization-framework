package it.pasqualecavallo.studentsmaterial.authorization_framework.filter;

import java.util.List;

public class AuthenticationContext {
	
	private AuthenticationContext() { }

	private static ThreadLocal<Principal> threadLocal = new ThreadLocal<>();

	public static void set(Principal principal) {
		AuthenticationContext.threadLocal.set(principal);
	}

	public static Principal get() {
		return threadLocal.get();
	}

	public static void remove() {
		threadLocal.remove();
	}

	public static class Principal {
		private String username;
		private List<String> roles;
		private Long userId;
		
		
		public Principal(String username, Long userId, List<String> roles) {
			super();
			this.username = username;
			this.roles = roles;
		}

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
		
		public Long getUserId() {
			return userId;
		}
		
		public void setUserId(Long userId) {
			this.userId = userId;
		}
	}

}
