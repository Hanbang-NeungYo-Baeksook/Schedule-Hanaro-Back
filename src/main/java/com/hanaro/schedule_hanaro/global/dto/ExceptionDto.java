package com.hanaro.schedule_hanaro.global.dto;

import com.hanaro.schedule_hanaro.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ExceptionDto {
	private final Integer code;
	private final String message;

	public ExceptionDto(ErrorCode errorCode) {
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
	}
}
