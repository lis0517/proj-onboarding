package com.proj.onboarding.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JwtAuthorizationFilter")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsServiceImpl userDetailsService;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException {

		try {
			String accessToken = jwtService.substringAccessToken(jwtService.getAccessTokenFromRequest(request));

			if (accessToken != null) {
				if (Boolean.TRUE.equals(jwtService.validateToken(accessToken))) {
					log.info("Access Token 유효");
					setAuthenticationContext(accessToken);
				} else {
					log.info("Invalid Token: {}", accessToken);
					sendExpiredTokenResponse(response);
					return;
				}
			}

		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token", e);
			sendExpiredTokenResponse(response);
			return;
		} catch (SecurityException | MalformedJwtException e) {
			log.error("Invalid JWT token", e);
			sendInvalidTokenResponse(response);
			return;
		} catch (Exception e) {
			log.error("Unexpected error during JWT processing", e);
			sendAuthenticationFailureResponse(response);
			return;
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
		return jwtService.isAuthorizationHeaderNotFound(request);
	}

	private void setAuthenticationContext(String accessToken) {
		String username = jwtService.extractUsernameFromToken(accessToken);

		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, "",
			userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private void sendExpiredTokenResponse(HttpServletResponse response) throws IOException {
		sendErrorResponse(response, "The access token has expired", "Expired-Token");
	}

	private void sendInvalidTokenResponse(HttpServletResponse response) throws IOException {
		sendErrorResponse(response, "The access token is invalid", "Invalid-Token");
	}

	private void sendAuthenticationFailureResponse(HttpServletResponse response) throws IOException {
		sendErrorResponse(response, "Authentication failed", "Authentication-Failed");
	}

	private void sendErrorResponse(HttpServletResponse response, String message, String error) throws IOException {
		int status = HttpServletResponse.SC_UNAUTHORIZED;

		response.setStatus(status);
		response.setContentType("application/json");

		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("status", status);
		responseBody.put("message", message);
		responseBody.put("data", error);

		response.getWriter().write(objectMapper.writeValueAsString(responseBody));
	}
}
