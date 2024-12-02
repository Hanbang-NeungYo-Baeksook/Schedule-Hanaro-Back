package com.hanaro.schedule_hanaro.global.dto;

import com.hanaro.schedule_hanaro.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ExceptionDto {
	private final Integer code;
	private final String message;

	private ExceptionDto(ErrorCode errorCode){
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
	}
	public static ExceptionDto of(ErrorCode errorCode){
		return new ExceptionDto(errorCode);
	}
}
