package com.proj.onboarding.domain.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proj.onboarding.domain.auth.dto.SignRequestDto;
import com.proj.onboarding.domain.auth.dto.SignResponseDto;
import com.proj.onboarding.domain.auth.dto.SignupRequestDto;
import com.proj.onboarding.domain.auth.dto.SignupResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User API")
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	@Operation(summary = "회원가입", description = "회원가입 할 때 사용하는 API")
	public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
		SignupResponseDto response = userService.signup(requestDto);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/sign")
	@Operation(summary = "로그인", description = "로그인에 사용하는 API")
	public ResponseEntity<SignResponseDto> sign(@RequestBody SignRequestDto requestDto) {
		SignResponseDto response = userService.sign(requestDto);
		return ResponseEntity.ok().body(response);
	}

}
