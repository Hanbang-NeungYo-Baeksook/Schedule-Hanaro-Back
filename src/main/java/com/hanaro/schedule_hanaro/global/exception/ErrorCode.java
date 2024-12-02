package com.hanaro.schedule_hanaro.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	// 403
	FORBIDDEN_REQUEST(40300, HttpStatus.FORBIDDEN, "권한이 존재하지 않습니다.");
	private final Integer code;
	private final HttpStatus httpStatus;
	private final String message;
}
