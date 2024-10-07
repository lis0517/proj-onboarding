package com.proj.onboarding.domain.auth;

import com.proj.onboarding.domain.auth.dto.SignRequestDto;
import com.proj.onboarding.domain.auth.dto.SignResponseDto;
import com.proj.onboarding.domain.auth.dto.SignupRequestDto;
import com.proj.onboarding.domain.auth.dto.SignupResponseDto;

public interface UserService {

	SignupResponseDto signup(SignupRequestDto requestDto);

	SignResponseDto sign(SignRequestDto loginRequestDto);

}
