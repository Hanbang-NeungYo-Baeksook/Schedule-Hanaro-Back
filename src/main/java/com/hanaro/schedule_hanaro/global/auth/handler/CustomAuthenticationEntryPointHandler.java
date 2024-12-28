package com.hanaro.schedule_hanaro.global.auth.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaro.schedule_hanaro.global.dto.ExceptionDto;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {
		System.out.println("exception 진입");
		ErrorCode errorCode = (ErrorCode)request.getAttribute("exception");
		System.out.println(errorCode);
		SecurityContextHolder.clearContext();
		handleException(response, errorCode);
	}

	private void handleException(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(new ObjectMapper().writeValueAsString(new ExceptionDto(errorCode)));
	}
}
