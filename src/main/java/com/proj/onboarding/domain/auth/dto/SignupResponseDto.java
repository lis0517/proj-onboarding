package com.proj.onboarding.domain.auth.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponseDto {
	private String username;
	private String nickname;
	private List<AuthorityDto> authorities;

	@Getter
	public static class AuthorityDto {
		private String authorityName;

		public AuthorityDto(String authorityName) {
			this.authorityName = authorityName;
		}
	}
}
