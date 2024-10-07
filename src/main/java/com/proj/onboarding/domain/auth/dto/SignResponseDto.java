package com.proj.onboarding.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignResponseDto {
	private String token;
}
