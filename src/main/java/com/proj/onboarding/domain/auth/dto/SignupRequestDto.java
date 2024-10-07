package com.proj.onboarding.domain.auth.dto;

import lombok.Getter;

@Getter
public class SignupRequestDto {
	private String username;
	private String nickname;
	private String password;
}
