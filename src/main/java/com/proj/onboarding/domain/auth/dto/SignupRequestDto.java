package com.proj.onboarding.domain.auth.dto;

import lombok.Getter;

@Getter
public class SignupRequestDto {
	private String username;
	private String nickname;
	private String password;

	public SignupRequestDto(String username, String nickname, String password) {
		this.username = username;
		this.nickname = nickname;
		this.password = password;
	}
}
