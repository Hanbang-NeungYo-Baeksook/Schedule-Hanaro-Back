package com.hanaro.schedule_hanaro.global.auth.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.hanaro.schedule_hanaro.global.exception.ErrorCode;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtExceptionFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (SecurityException e) {
			logger.error("JwtExceptionFilter throw Security Exception : ");
			request.setAttribute("exception", ErrorCode.FORBIDDEN_REQUEST);
			filterChain.doFilter(request, response);
		} catch (MalformedJwtException e) {
			logger.error("JwtExceptionFilter throw Malformed Jwt Exception : ");
			request.setAttribute("exception", ErrorCode.MALFOREMD_ACCESS_TOKEN);
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			logger.error("JwtExceptionFilter throw Expired Jwt Exception : ");
			request.setAttribute("exception", ErrorCode.EXPIRED_ACCESS_TOKEN);
			filterChain.doFilter(request, response);
		}
	}
}
