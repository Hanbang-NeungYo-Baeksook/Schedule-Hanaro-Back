package com.hanaro.schedule_hanaro.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Exception extends RuntimeException{
	private final ErrorCode errorCode;
	public String getMessage(){
		return this.errorCode.getMessage();
	}
}
