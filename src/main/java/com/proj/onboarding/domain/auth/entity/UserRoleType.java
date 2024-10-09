package com.proj.onboarding.domain.auth.entity;

public enum UserRoleType {
	USER("ROLE_USER"),
	ADMIN("ROLE_ADMIN"),
	MODERATOR("ROLE_MODERATOR");

	private final String roleName;

	UserRoleType(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}
}
