package com.proj.onboarding;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.proj.onboarding.domain.auth.entity.UserRoleType;
import com.proj.onboarding.security.JwtProvider;

// TestInstance는 테스트 인스턴스의 라이프 사이클을 설정할 때 사용
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtTokenTest {

	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER = "Authorization";
	public static final String AUTHORIZATION_KEY = "auth";
	public static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshToken";

	private String secretKey;
	private String base64EncodedSecretKey;

	@BeforeEach
	void setUp() {
		secretKey = "leeyoonseong'sonboardingproject!";
		base64EncodedSecretKey = JwtProvider.encodeBase64SecretKey(secretKey);
	}

	@Test
	void generateAccessTokenTest() {

		String token = JwtProvider.generateAccessToken("auth", List.of(UserRoleType.USER.getRoleName()), "lis0517",
			3000000L, base64EncodedSecretKey);

		System.out.println(token);

		assertNotNull(token);

		assertFalse(token.isEmpty());
	}

	@Test
	void generateRefreshTokenTest() {
		String token = JwtProvider.generateRefreshToken("lis0517", 3000000L, base64EncodedSecretKey);

		System.out.println(token);

		assertNotNull(token);

		assertFalse(token.isEmpty());
	}

	@Test
	void validateTokenTest() {

		String accessToken = JwtProvider.generateAccessToken("auth", List.of(UserRoleType.USER.getRoleName()),
			"lis0517", 3000000L, base64EncodedSecretKey);
		String refreshToken = JwtProvider.generateRefreshToken("lis0517", 3000000L, base64EncodedSecretKey);

		assertTrue(JwtProvider.validateToken(accessToken, base64EncodedSecretKey));
		assertTrue(JwtProvider.validateToken(refreshToken, base64EncodedSecretKey));
	}

}
