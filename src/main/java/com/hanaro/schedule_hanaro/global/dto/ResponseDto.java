package com.hanaro.schedule_hanaro.global.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record ResponseDto<T>(
	@JsonIgnore HttpStatus httpStatus,
	@NotNull boolean success,
	@Nullable T data,
	@Nullable ExceptionDto error
	) {
	public static <T> ResponseDto<T> ok(T data){
		return new ResponseDto<>(
			HttpStatus.OK,
			true,
			data,
			null
		);
	}
	public static <T> ResponseDto<T> created(T data){
		return new ResponseDto<>(
			HttpStatus.CREATED,
			true,
			data
			,null
		);
	}
	public static ResponseDto<Object> fail(@NotNull GlobalException e){
		return new ResponseDto<>(
			e.getErrorCode().getHttpStatus(),
			false,
			null,
			ExceptionDto.of(e.getErrorCode())
		);
	}
}
