package com.hanaro.schedule_hanaro.global.auth.message;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 401 UNAUTHORIZED : 인증 안됨
	EMPTY_JWT(UNAUTHORIZED,40101,"토큰이 존재하지 않습니다."),
	EXPIRED_JWT_TOKEN(UNAUTHORIZED,40102,"토큰이 만료되었습니다."),
	INVALID_JWT_TOKEN(UNAUTHORIZED, 40103, "올바르지 않은 토큰입니다."),
	UNAUTHENTICATED(UNAUTHORIZED,40104,"인증 과정 중 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}
