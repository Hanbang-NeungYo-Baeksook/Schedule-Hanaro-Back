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
			System.out.println("시큐리티");
			logger.error("JwtExceptionFilter throw Security Exception : ");
			request.setAttribute("exception", ErrorCode.FORBIDDEN_REQUEST);
			filterChain.doFilter(request, response);
		} catch (MalformedJwtException e) {
			System.out.println("malform");
			logger.error("JwtExceptionFilter throw Malformed Jwt Exception : ");
			request.setAttribute("exception", ErrorCode.MALFORMED_ACCESS_TOKEN);
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			System.out.println("expired");
			logger.error("JwtExceptionFilter throw Expired Jwt Exception : ");
			request.setAttribute("exception", ErrorCode.EXPIRED_ACCESS_TOKEN);
			filterChain.doFilter(request, response);
		} catch (UnsupportedJwtException e) {
			System.out.println("unsupported");
			logger.error("JwtExceptionFilter throw Unsupported Jwt Exception : ");
			request.setAttribute("exception", ErrorCode.UNSUPPORTED_TOKEN);
			filterChain.doFilter(request, response);
		} catch (IllegalArgumentException e) {
			System.out.println("illegal");
			logger.error("JwtExceptionFilter throw Illegal Argument Exception : ");
			request.setAttribute("exception", ErrorCode.UNSUPPORTED_TOKEN);
			filterChain.doFilter(request, response);
		} catch (GlobalException e) {
			System.out.println("global");
			logger.error("JwtExceptionFilter throw Global Exception : ");
			request.setAttribute("exception", e.getErrorCode());
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			System.out.println("exception");
			logger.error("JwtExceptionFilter throw Exception : ");
			request.setAttribute("exception", ErrorCode.NOT_FOUND_DATA);
			filterChain.doFilter(request, response);
		}
	}
}
