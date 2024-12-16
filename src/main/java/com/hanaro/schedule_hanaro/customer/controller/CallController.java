package com.hanaro.schedule_hanaro.customer.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.hanaro.schedule_hanaro.customer.dto.request.CallRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.CallDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.ErrorResponse;
import com.hanaro.schedule_hanaro.customer.service.CallService;
import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
import com.hanaro.schedule_hanaro.global.auth.info.UserInfo;

@RestController
@RequestMapping("/api/calls")
public class CallController {

	@Autowired
	private CallService callService;

	@PostMapping
	public ResponseEntity<?> createCall(
		@RequestBody CallRequest request, Principal principal
	) {
		try {
			String customerAuthId = principal.getName();

			CallResponse response = callService.createCall(customerAuthId, request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (RuntimeException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("401", e.getMessage()));
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
		@RequestParam(value = "status", defaultValue = "pending") String status,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		CallListResponse response = callService.getCallList(status, page, size);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{call_id}")
	public ResponseEntity<?> getCallDetail(
		@RequestHeader(value = "Authorization", required = false) String authorization,
		@PathVariable("call_id") Long callId
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
