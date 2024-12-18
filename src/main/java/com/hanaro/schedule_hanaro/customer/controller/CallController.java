package com.hanaro.schedule_hanaro.customer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.hanaro.schedule_hanaro.customer.dto.request.CallRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.CallDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.ErrorResponse;
import com.hanaro.schedule_hanaro.customer.service.CallService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Call", description = "전화 상담 API")
@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
public class CallController {

	private final CallService callService;

	@Operation(summary = "전화 상담 생성", description = "새로운 상담을 생성합니다.")
	@PostMapping
	public ResponseEntity<?> createCall(
		Authentication authentication,
		@RequestBody CallRequest request
	) {
		try {
			CallResponse response = callService.createCall(authentication, request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("2020102", e.getMessage()));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("2010203", e.getMessage()));
		}
	}

	@DeleteMapping("/{call-id}")
	public ResponseEntity<?> cancelCall(@PathVariable("call-id") Long callId) {
		try {
			callService.cancelCall(callId);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse("2010301", "존재하지 않는 상담 데이터입니다."));
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse("2010303", e.getMessage()));
		}
	}

	@GetMapping
	public ResponseEntity<CallListResponse> getCallList(
		@RequestHeader(value = "Authorization", required = false) String authorization,
		@RequestParam(value = "status", defaultValue = "pending") String status,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		CallListResponse response = callService.getCallList(status, page, size);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{call-id}")
	public ResponseEntity<?> getCallDetail(
		@PathVariable("call-id") Long callId
	) {
		try {
			CallDetailResponse response = callService.getCallDetail(callId);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(404)
				.body(new ErrorResponse("2010201", "존재하지 않는 상담 데이터입니다."));
		}
	}
}
