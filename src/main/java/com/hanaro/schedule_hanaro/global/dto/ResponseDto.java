package com.hanaro.schedule_hanaro.global.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record ResponseDto<T>(
	@JsonIgnore HttpStatus httpStatus,
	@NotNull boolean success,
	@Nullable T data
	) {
	public static <T> ResponseDto<T> ok(T data){
		return new ResponseDto<>(
			HttpStatus.OK,
			true,
			data
		);
	}
	public static <T> ResponseDto<T> created(T data){
		return new ResponseDto<>(
			HttpStatus.CREATED,
			true,
			data
		);
	}
}
