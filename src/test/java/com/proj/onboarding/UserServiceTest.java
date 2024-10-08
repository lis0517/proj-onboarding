package com.proj.onboarding;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.proj.onboarding.domain.auth.UserRepository;
import com.proj.onboarding.domain.auth.UserRoleRepository;
import com.proj.onboarding.domain.auth.UserServiceImpl;
import com.proj.onboarding.domain.auth.dto.SignRequestDto;
import com.proj.onboarding.domain.auth.dto.SignResponseDto;
import com.proj.onboarding.domain.auth.dto.SignupRequestDto;
import com.proj.onboarding.domain.auth.dto.SignupResponseDto;
import com.proj.onboarding.domain.auth.entity.User;
import com.proj.onboarding.domain.auth.entity.UserRole;
import com.proj.onboarding.security.JwtService;

class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserRoleRepository userRoleRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private UserServiceImpl userService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSignup() {
		SignupRequestDto requestDto = new SignupRequestDto("testuser", "password", "Test User");
		User user = new User("testuser", "Test User", "encodedPassword");
		UserRole role = new UserRole("ROLE_USER");

		when(userRepository.existsByUsername("testuser")).thenReturn(false);
		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
		when(userRoleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
		when(userRepository.save(any(User.class))).thenReturn(user);

		SignupResponseDto responseDto = userService.signup(requestDto);

		assertNotNull(responseDto);
		assertEquals("testuser", responseDto.getUsername());
		assertEquals("Test User", responseDto.getNickname());
	}

	@Test
	void testSign() {
		SignRequestDto requestDto = new SignRequestDto("testuser", "password");
		User user = new User("testuser", "Test User", "encodedPassword");
		UserRole role = new UserRole("ROLE_USER");
		user.addRole(role);

		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
		when(jwtService.generateAccessToken(anyString(), anyList())).thenReturn("access_token");
		when(jwtService.generateRefreshToken(anyString())).thenReturn("refresh_token");

		SignResponseDto responseDto = userService.sign(requestDto);

		assertNotNull(responseDto);
		assertEquals("access_token", responseDto.getToken());
	}

}
