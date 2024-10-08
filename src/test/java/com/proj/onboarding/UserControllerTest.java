package com.proj.onboarding;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.onboarding.config.SecurityConfig;
import com.proj.onboarding.domain.auth.UserController;
import com.proj.onboarding.domain.auth.UserService;
import com.proj.onboarding.domain.auth.dto.SignRequestDto;
import com.proj.onboarding.domain.auth.dto.SignResponseDto;
import com.proj.onboarding.domain.auth.dto.SignupRequestDto;
import com.proj.onboarding.domain.auth.dto.SignupResponseDto;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testSignup() throws Exception {
		SignupRequestDto requestDto = new SignupRequestDto("testuser", "password", "Test User");
		SignupResponseDto responseDto = SignupResponseDto.builder()
			.username("testuser")
			.nickname("Test User")
			.authorities(null)
			.build();

		when(userService.signup(any(SignupRequestDto.class))).thenReturn(responseDto);

		mockMvc.perform(post("/api/signup").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("testuser"))
			.andExpect(jsonPath("$.nickname").value("Test User"));
	}

	@Test
	void testSign() throws Exception {
		SignRequestDto requestDto = new SignRequestDto("testuser", "password");
		SignResponseDto responseDto = SignResponseDto.builder().token("token").build();

		when(userService.sign(any(SignRequestDto.class))).thenReturn(responseDto);

		mockMvc.perform(post("/api/sign")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value("token"));
	}
}
