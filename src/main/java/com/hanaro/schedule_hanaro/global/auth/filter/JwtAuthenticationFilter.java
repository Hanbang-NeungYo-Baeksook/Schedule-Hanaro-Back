package com.hanaro.schedule_hanaro.global.auth.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
import com.hanaro.schedule_hanaro.global.auth.info.UserInfo;
import com.hanaro.schedule_hanaro.global.auth.provider.JwtAuthenticationProvider;
import com.hanaro.schedule_hanaro.global.auth.provider.JwtTokenProvider;
import com.hanaro.schedule_hanaro.global.domain.enums.Role;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
		System.out.println("jwtauthenticationfilter 진입");

		// Swagger 및 OpenAPI 경로에 대해 필터 적용 제외
		String requestURI = request.getRequestURI();
		if (requestURI.startsWith("/swagger-ui") || requestURI.startsWith("/v3/api-docs")) {
			filterChain.doFilter(request, response);
			return;
		}

		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request,response);
			return;
		}
		String token = header.substring(7);

		Claims claims = jwtTokenProvider.validateToken(token);
		UserInfo userInfo = new UserInfo(claims.getId(), Role.valueOf(claims.get("role").toString()));
		System.out.println("jwtauthenticationfilter: " + userInfo.id());
		UsernamePasswordAuthenticationToken unAuthenticatedToken = new UsernamePasswordAuthenticationToken(userInfo,
			null, null);
		System.out.println("이어서: " + unAuthenticatedToken.getPrincipal());
		UsernamePasswordAuthenticationToken authenticatedToken = (UsernamePasswordAuthenticationToken)jwtAuthenticationProvider.authenticate(
			unAuthenticatedToken);

		System.out.println("authenticate token 완료" + authenticatedToken.getPrincipal());
		System.out.println(authenticatedToken.getCredentials());
		System.out.println(authenticatedToken.getName());
		System.out.println(authenticatedToken.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
		System.out.println("context 설정 완료" + SecurityContextHolder.getContext().toString());

		filterChain.doFilter(request, response);
	}
}
