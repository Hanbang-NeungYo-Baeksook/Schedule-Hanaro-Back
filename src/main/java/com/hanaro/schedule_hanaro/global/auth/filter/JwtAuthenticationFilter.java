package com.hanaro.schedule_hanaro.global.auth.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hanaro.schedule_hanaro.global.auth.info.UserInfo;
import com.hanaro.schedule_hanaro.global.auth.provider.JwtAuthenticationProvider;
import com.hanaro.schedule_hanaro.global.auth.provider.JwtTokenProvider;
import com.hanaro.schedule_hanaro.global.domain.enums.Role;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtAuthenticationProvider jwtAuthenticationProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		if (request.getRequestURI().equals("/api/auth/sign-in")) {
			filterChain.doFilter(request, response);
			return;
		}

		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer "))
			throw new RuntimeException("올바르지 않은 토큰입니다.");
		String token = header.substring(7);

		Claims claims = jwtTokenProvider.validateToken(token);
		UserInfo userInfo = new UserInfo(claims.getId(), Role.valueOf(claims.get("role").toString()));

		UsernamePasswordAuthenticationToken unAuthenticatedToken = new UsernamePasswordAuthenticationToken(userInfo,
			null, null);

		UsernamePasswordAuthenticationToken authenticatedToken = (UsernamePasswordAuthenticationToken)jwtAuthenticationProvider.authenticate(
			unAuthenticatedToken);

		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authenticatedToken);
		SecurityContextHolder.setContext(securityContext);

		filterChain.doFilter(request, response);
	}
}
