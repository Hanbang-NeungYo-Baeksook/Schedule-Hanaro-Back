package com.hanaro.schedule_hanaro.global.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hanaro.schedule_hanaro.global.dto.ExceptionDto;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;


@RestControllerAdvice
public class ErrorController {

	@ExceptionHandler(GlobalException.class)
	@ResponseBody
	public ResponseEntity<ExceptionDto> handleGlobalException(GlobalException e) {
		return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(new ExceptionDto(e.getErrorCode()));
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	@ResponseBody
	public ResponseEntity<?> handleError(RuntimeException e) {
		return ResponseEntity.status(500).body(e.getMessage());
	}
}
