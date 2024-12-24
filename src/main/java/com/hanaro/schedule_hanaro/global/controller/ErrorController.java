package com.hanaro.schedule_hanaro.global.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.hanaro.schedule_hanaro.global.dto.ExceptionDto;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;

@RestControllerAdvice
public class ErrorController {

	@ExceptionHandler(GlobalException.class)
	@ResponseBody
	public ResponseEntity<ExceptionDto> handleGlobalException(GlobalException e) {
		return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(new ExceptionDto(e.getErrorCode()));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseBody
	public ResponseEntity<ExceptionDto> handleParameterException(MissingServletRequestParameterException e) {
		return ResponseEntity.badRequest().body(new ExceptionDto(ErrorCode.MISSING_REQUEST_PARAMETER));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseBody
	public ResponseEntity<ExceptionDto> handleMethodTypeException(MethodArgumentTypeMismatchException e) {
		return ResponseEntity.badRequest().body(new ExceptionDto(ErrorCode.MISSING_REQUEST_PARAMETER));
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	@ResponseBody
	public ResponseEntity<?> handleError(RuntimeException e) {
		return ResponseEntity.status(500).body(e.getMessage());
	}
}
