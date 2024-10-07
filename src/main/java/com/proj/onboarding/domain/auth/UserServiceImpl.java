package com.proj.onboarding.domain.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proj.onboarding.common.exception.DuplicateUsernameException;
import com.proj.onboarding.common.exception.InvalidCredentialsException;
import com.proj.onboarding.common.exception.RoleNotFoundException;
import com.proj.onboarding.common.exception.UserNotFoundException;
import com.proj.onboarding.domain.auth.dto.SignRequestDto;
import com.proj.onboarding.domain.auth.dto.SignResponseDto;
import com.proj.onboarding.domain.auth.dto.SignupRequestDto;
import com.proj.onboarding.domain.auth.dto.SignupResponseDto;
import com.proj.onboarding.domain.auth.entity.User;
import com.proj.onboarding.domain.auth.entity.UserRole;
import com.proj.onboarding.domain.auth.entity.UserRoleType;
import com.proj.onboarding.security.JwtService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "UserServiceImpl")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Override
	@Transactional
	public SignupResponseDto signup(SignupRequestDto requestDto) {
		log.info("Attempting to sign up user : {}", requestDto.getUsername());

		existsUsername(requestDto.getUsername());

		User user = createUser(requestDto);
		assignUserRole(user);

		User savedUser = userRepository.save(user);
		log.info("User successfully signed up : {}", savedUser.getUsername());

		return savedUser.toDto();
	}

	@Override
	@Transactional
	public SignResponseDto sign(SignRequestDto requestDto) {
		log.info("Attempting to sign user : {}", requestDto.getUsername());

		User user = getUserByUsername(requestDto.getUsername());

		validatePassword(requestDto.getPassword(), user.getPassword());

		String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getRoles().stream().map(UserRole::getName).toList());
		String refreshToken = jwtService.generateRefreshToken(user.getUsername());

		user.updateRefreshToken(refreshToken);

		jwtService.setRefreshTokenAtCookie(refreshToken);
		jwtService.setAccessTokenAtHeader(accessToken);

		log.info("User Successfully to sign : username : {} / token : {}", requestDto.getUsername(), accessToken);
		return SignResponseDto.builder().token(accessToken).build();
	}

	@Override
	public User findUserByUsername(String username) {
		return getUserByUsername(username);
	}

	private void existsUsername(String username) {
		if (userRepository.existsByUsername(username)) {
			log.warn("Signup with existing username : {}", username);
			throw new DuplicateUsernameException("Invalid username.");
		}
	}

	private User getUserByUsername(String username) {
		return userRepository.findByUsername(username).orElseThrow(() -> {
			log.warn("Sign attempt with non-existent username : {}", username);
			return new UserNotFoundException("Invalid username or password.");
		});
	}

	private void validatePassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			log.warn("Sign attempt with incorrect password.");
			throw new InvalidCredentialsException("Invalid username or password.");
		}
	}

	private User createUser(SignupRequestDto requestDto) {
		String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

		return User.builder()
			.username(requestDto.getUsername())
			.password(encodedPassword)
			.nickname(requestDto.getNickname())
			.build();
	}

	private void assignUserRole(User user) {
		UserRole userRole = userRoleRepository.findByName(UserRoleType.USER.getRoleName())
			.orElseThrow(() -> new RoleNotFoundException("Invalid role."));
		user.addRole(userRole);
		log.info("Assigned ROLE_USER to user : {}", user.getUsername());
	}

}
