package com.hanaro.schedule_hanaro.global.auth.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaro.schedule_hanaro.global.dto.ExceptionDto;
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
			System.out.println("시큐리티");
			logger.error("JwtExceptionFilter throw Security Exception : ");
			handleException(response, ErrorCode.FORBIDDEN_REQUEST);
		} catch (MalformedJwtException e) {
			System.out.println("malform");
			logger.error("JwtExceptionFilter throw Malformed Jwt Exception : ");
			handleException(response, ErrorCode.MALFORMED_ACCESS_TOKEN);
		} catch (ExpiredJwtException e) {
			System.out.println("expired");
			logger.error("JwtExceptionFilter throw Expired Jwt Exception : ");
			handleException(response, ErrorCode.EXPIRED_ACCESS_TOKEN);
		} catch (UnsupportedJwtException e) {
			System.out.println("unsupported");
			logger.error("JwtExceptionFilter throw Unsupported Jwt Exception : ");
			handleException(response, ErrorCode.UNSUPPORTED_TOKEN);
		} catch (IllegalArgumentException e) {
			System.out.println("illegal");
			logger.error("JwtExceptionFilter throw Illegal Argument Exception : ");
			handleException(response, ErrorCode.UNSUPPORTED_TOKEN);
		} catch (GlobalException e) {
			System.out.println("global");
			logger.error("JwtExceptionFilter throw Global Exception : ");
			handleException(response, e.getErrorCode());
		} catch (Exception e) {
			System.out.println("exception");
			logger.error("JwtExceptionFilter throw Exception : ");
			handleException(response, ErrorCode.NOT_FOUND_DATA);
		}
	}

	private void handleException(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(new ObjectMapper().writeValueAsString(new ExceptionDto(errorCode)));
	}
}
