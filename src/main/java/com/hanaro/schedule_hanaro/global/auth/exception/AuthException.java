package com.hanaro.schedule_hanaro.global.auth.exception;

import com.hanaro.schedule_hanaro.global.auth.message.ErrorCode;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException{
	private final ErrorCode errorCode;

	public AuthException(ErrorCode errorCode) {
		super("[AuthException]: " + errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
