package com.hanaro.schedule_hanaro.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.hanaro.schedule_hanaro.customer.dto.request.CallRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.CallResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.ErrorResponse;
import com.hanaro.schedule_hanaro.customer.service.CallService;

@RestController
@RequestMapping("/api/calls")
public class CallController {

	@Autowired
	private CallService callService;

	@PostMapping
	public ResponseEntity<?> createCall(
		@RequestHeader("Authorization") String authorization,
		@RequestBody CallRequest request
	) {
		try {
			Long customerId = extractCustomerIdFromToken(authorization);
			CallResponse response = callService.createCall(customerId, request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("2020102", e.getMessage()));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("2010203", e.getMessage()));
		}
	}


	// 추후 수정 - 실제 구현에서는 토큰 파싱 로직 필요
	private Long extractCustomerIdFromToken(String token) {
		return 1L;
	}

}
