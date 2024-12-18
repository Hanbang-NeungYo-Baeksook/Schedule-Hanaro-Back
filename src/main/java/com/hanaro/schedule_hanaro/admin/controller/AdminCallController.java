package com.hanaro.schedule_hanaro.admin.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.admin.dto.request.AdminCallMemoRequest;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallHistoryListResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallWaitResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminCallService;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin-Call", description = "관리자 전화 상담 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/calls")
public class AdminCallController {
	private final AdminCallService callService;

	@GetMapping("/wait")
	public ResponseEntity<AdminCallWaitResponse> getCallWaitList() {
		// 전화 상담 대기 목록
		return ResponseEntity.ok(callService.findWaitList());
	}

	@PatchMapping("/{call-id}")
	public ResponseEntity<String> patchCallStatus(@PathVariable("call-id") Long callId) {
		// 전화 상담 상태 변경
		try {
			return ResponseEntity.ok(callService.changeCallStatus(callId));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/{call-id}")
	public ResponseEntity<String> postCallMemo(@PathVariable("call-id") Long callId, @RequestBody AdminCallMemoRequest request) {
		// 전화 상담 메모 등록
		return ResponseEntity.ok(callService.saveCallMemo(callId, request.content()));
	}

	@GetMapping()
	public ResponseEntity<AdminCallHistoryListResponse> getCallList(
		@RequestParam(value = "status", defaultValue = "pending") Status status,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "5") int size,
		@RequestParam(required = false) LocalDate startedAt,
		@RequestParam(required = false) LocalDate endedAt,
		@RequestParam(required = false) Category category,
		@RequestParam(required = false) String keyword
	) {
		// 전화 상담 목록 조회
		return ResponseEntity.ok(callService.findFilteredCalls(page, size, status, startedAt, endedAt, category, keyword));
	}

	@GetMapping("/{call-id}")
	public ResponseEntity<?> getCallDetail(
		@PathVariable("call-id") Long callId
	) {
		// 전화 상담 상세 조회
		try {
			return ResponseEntity.ok(callService.findCall(callId));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
}
