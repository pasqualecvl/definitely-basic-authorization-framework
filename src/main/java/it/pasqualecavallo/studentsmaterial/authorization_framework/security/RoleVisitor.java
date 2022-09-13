package it.pasqualecavallo.studentsmaterial.authorization_framework.security;

import java.util.List;

public abstract class RoleVisitor {

	public boolean isRoleHierarchicallyUpperOrEqualsTo(String requiredRole, List<String> userRoles) {
		for(String userRole : userRoles) {
			if(isRoleInternalHierarchicallyUpperOrEqualsTo(requiredRole, userRole)) {
				return true;
			}
		}
		return false;
	}

	protected abstract boolean isRoleInternalHierarchicallyUpperOrEqualsTo(String requiredRole, String userRoles);

}
