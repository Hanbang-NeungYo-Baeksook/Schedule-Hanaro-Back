package com.hanaro.schedule_hanaro.global.auth.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
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
			request.setAttribute("exception", ErrorCode.MALFORMED_ACCESS_TOKEN);
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			logger.error("JwtExceptionFilter throw Expired Jwt Exception : ");
			request.setAttribute("exception", ErrorCode.EXPIRED_ACCESS_TOKEN);
			filterChain.doFilter(request, response);
		} catch (UnsupportedJwtException e) {
			logger.error("JwtExceptionFilter throw Unsupported Jwt Exception : ");
			request.setAttribute("exception", ErrorCode.UNSUPPORTED_TOKEN);
			filterChain.doFilter(request, response);
		} catch (IllegalArgumentException e) {
			logger.error("JwtExceptionFilter throw Illegal Argument Exception : ");
			request.setAttribute("exception", ErrorCode.UNSUPPORTED_TOKEN);
			filterChain.doFilter(request, response);
		} catch (GlobalException e) {
			logger.error("JwtExceptionFilter throw Illegal Argument Exception : ");
			request.setAttribute("exception", e.getErrorCode());
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			logger.error("JwtExceptionFilter throw Exception : ");
			request.setAttribute("exception", ErrorCode.NOT_FOUND_DATA);
			filterChain.doFilter(request, response);
		}
	}
}
