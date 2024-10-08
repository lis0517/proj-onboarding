package com.proj.onboarding.security;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JwtService")
@Service
public class JwtService {

	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER = "Authorization";
	public static final String AUTHORIZATION_KEY = "auth";
	public static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshToken";

	@Value("${jwt.secret.key}")
	private String secretKey;

	@Value("${access.token.expiration}")
	private Long accessTokenExpiration; // access token 만료 시간

	@Value("${refresh.token.expiration}")
	private Long refreshTokenExpiration; // refresh token 만료 시간

	public String generateAccessToken(String username, List<String> roles) {

		return JwtProvider.generateAccessToken(AUTHORIZATION_KEY, roles, username, accessTokenExpiration,
			getEncodedBase64SecretKey());
	}

	public String generateRefreshToken(String username) {
		return JwtProvider.generateRefreshToken(username, refreshTokenExpiration, getEncodedBase64SecretKey());
	}

	private String getEncodedBase64SecretKey() {
		return JwtProvider.encodeBase64SecretKey(secretKey);
	}

	/**
	 * "Authorization" 헤더 값 Token Substring
	 *
	 * @param tokenValue Authorization 헤더의 토큰 값
	 * @return "Bearer " 문구 자른 순수한 Token 값
	 */
	public String substringAccessToken(String tokenValue) {

		if (tokenValue.startsWith(TOKEN_PREFIX)) {
			return tokenValue.substring(7);
		}
		return null;
	}

	/**
	 * Http 요청에서 Access Token 추출합니다.
	 *
	 * @param request Http 요청
	 * @return 추출된 Access Token
	 */
	public String getAccessTokenFromRequest(HttpServletRequest request) {

		return request.getHeader(HEADER);
	}

	/**
	 * HTTP 응답 헤더에 Access 토큰을 저장합니다.
	 *
	 * @param accessToken 설정할 Access 토큰
	 */
	public void setAccessTokenAtHeader(String accessToken) {
		HttpServletResponse response = ((ServletRequestAttributes)Objects.requireNonNull(
			RequestContextHolder.getRequestAttributes())).getResponse();
		if (response != null) {
			response.setHeader(HEADER, TOKEN_PREFIX + accessToken);
		}
	}

	public Boolean validateToken(String token) {

		return JwtProvider.validateToken(token, getEncodedBase64SecretKey());
	}

	/**
	 * JWT 토큰에서 username을 추출합니다.
	 *
	 * @param token JWT 토큰
	 * @return 추출된 이메일
	 */
	public String extractUsernameFromToken(String token) {
		return JwtProvider.extractAllClaims(token, getEncodedBase64SecretKey()).getSubject();
	}

	/**
	 * 헤더에 토큰이 있는지 확인합니다.
	 *
	 * @param request HttpServletRequest
	 * @return Authorization header 존재 여부
	 */
	public Boolean isAuthorizationHeaderNotFound(HttpServletRequest request) {
		return request.getHeader(HEADER) == null;
	}

	/**
	 * Refresh 토큰을 쿠키에 저장합니다.
	 *
	 * @param refreshToken 저장할 Refresh 토큰
	 */
	public void setRefreshTokenAtCookie(String refreshToken) {
		Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge((int)(refreshTokenExpiration / 1000));

		HttpServletResponse response = ((ServletRequestAttributes)Objects.requireNonNull(
			RequestContextHolder.getRequestAttributes())).getResponse();

		if (response != null) {
			response.addCookie(cookie);
		}
	}

}
