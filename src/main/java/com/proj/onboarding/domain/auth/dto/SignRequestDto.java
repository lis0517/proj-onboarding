package com.proj.onboarding.domain.auth.dto;

import lombok.Getter;

@Getter
public class SignRequestDto {
	private String username;
	private String password;

	public SignRequestDto(String username, String password) {
		this.username = username;
		this.password = password;
	}
}
