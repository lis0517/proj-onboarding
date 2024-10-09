package com.proj.onboarding.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

public final class JwtProvider {

	private JwtProvider() {
		// 인스턴스화 방지 기본 생성자
	}

	/**
	 * JWT Access Token 생성
	 *
	 * @param authorizationKey       역할 정보가 저장될 클레임 키
	 * @param role                   사용자 역할
	 * @param username               사용자 이름
	 * @param expiration             토큰 만료 시간 (밀리초)
	 * @param base64EncodedSecretKey 토큰 서명 알고리즘
	 * @return 생성된 JWT Access Token
	 */
	public static String generateAccessToken(String authorizationKey, Object role, String username, long expiration,
		String base64EncodedSecretKey) {

		Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

		return Jwts.builder()
			.claim(authorizationKey, role)
			.setSubject(username)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expiration))
			.signWith(key)
			.compact();
	}

	/**
	 * JWT Refresh Token 생성
	 *
	 * @param username               사용자 이름
	 * @param expiration             토큰 만료 시간 (밀리초)
	 * @param base64EncodedSecretKey 토큰 서명을 위한 비밀 키
	 * @return 생성된 JWT Refresh Token
	 */
	public static String generateRefreshToken(String username, long expiration, String base64EncodedSecretKey) {

		Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

		return Jwts.builder()
			.setSubject(username)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expiration))
			.signWith(key)
			.compact();
	}

	public static String encodeBase64SecretKey(String secretKey) {
		return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 토큰에서 모든 클레임 추출
	 *
	 * @param token     JWT 토큰
	 * @param secretKey 토큰 검증을 위한 비밀 키
	 * @return 추출된 모든 클레임
	 */
	public static Claims extractAllClaims(String token, String secretKey) {
		return Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build().parseClaimsJws(token).getBody();
	}

	private static Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);

		return Keys.hmacShaKeyFor(keyBytes);
	}

	public static Boolean validateToken(String token, String base64EncodedSecretKey) {
		try {
			Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException | IllegalArgumentException | ExpiredJwtException |
				 UnsupportedJwtException ignored) {
			return false;
		}
	}

}
