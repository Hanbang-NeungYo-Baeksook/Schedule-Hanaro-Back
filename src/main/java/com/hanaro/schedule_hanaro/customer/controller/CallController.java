package com.hanaro.schedule_hanaro.customer.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.hanaro.schedule_hanaro.customer.dto.request.CallRequest;
import com.hanaro.schedule_hanaro.customer.dto.request.TimeSlotAvailabilityRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.CallDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.TimeSlotAvailabilityResponse;
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

	@Operation(summary = "전화 상담 예약 생성", description = "새로운 전화 상담 예약을 생성합니다.")
	@PostMapping
	public ResponseEntity<?> createCall(
		Authentication authentication,
		@RequestBody CallRequest request
	) throws InterruptedException {
		CallResponse response = callService.createCall(authentication, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}


	@Operation(summary = "전화 상담 예약 취소", description = "특정 전화 상담 예약을 취소합니다.")
	@DeleteMapping("/{call-id}")
	public ResponseEntity<?> cancelCall(@PathVariable("call-id") Long callId) {
		callService.cancelCall(callId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "전화 상담 시간대 가능 인원 반환", description = "전화 상담 시간대의 가능 인원 반환합니다.")
	@PostMapping("/availability")
	public ResponseEntity<Map<String, Object>> getTimeSlotAvailability(
		@RequestBody TimeSlotAvailabilityRequest request) {
		List<TimeSlotAvailabilityResponse> responses = callService.getTimeSlotAvailability(request);

		Map<String, Object> responseMap = Map.of(
			"data", responses
		);

		return ResponseEntity.ok().body(responseMap);
	}

	@Operation(summary = "전화 상담 목록 조회", description = "전화 상담 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<CallListResponse> getCallList(
		Authentication authentication,
		@RequestParam(value = "status", defaultValue = "pending") String status,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		CallListResponse response = callService.getCallList(authentication, status, page, size);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "전화 상담 정보 상세", description = "특정 전화 상담의 상세 정보를 조회합니다.")
	@GetMapping("/{call-id}")
	public ResponseEntity<Map<String, CallDetailResponse>> getCallDetail(
		@PathVariable("call-id") Long callId
	) {
		CallDetailResponse response = callService.getCallDetail(callId);
		Map<String, CallDetailResponse> wrappedResponse = Map.of("data", response);
		return ResponseEntity.ok().body(wrappedResponse);
	}
}
