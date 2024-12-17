package com.hanaro.schedule_hanaro.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class GlobalException extends RuntimeException{
	private final ErrorCode errorCode;
	private final String message;

	public GlobalException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.message = errorCode.getMessage();
	}
	public GlobalException(ErrorCode errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}
}
