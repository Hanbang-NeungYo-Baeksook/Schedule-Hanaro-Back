package com.hanaro.schedule_hanaro.admin.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallHistoryListResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallHistoryResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallWaitResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminCallService;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;

import lombok.RequiredArgsConstructor;

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

	@PatchMapping("/{callId}")
	public ResponseEntity<String> patchCallStatus(@PathVariable Long callId) {
		// 전화 상담 상태 변경
		try {
			return ResponseEntity.ok(callService.changeCallStatus(callId));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (IllegalStateException e) {
			return ResponseEntity.status(405).body(e.getMessage());
		}
	}

	@PostMapping("/{callId}")
	public ResponseEntity<String> postCallMemo(@PathVariable Long callId, @RequestBody String content) {
		// 전화 상담 메모 등록
		// TODO: call_memo table에 id 추가
		return ResponseEntity.ok(callService.saveCallMemo(callId, content));
	}

	@GetMapping()
	public ResponseEntity<AdminCallHistoryListResponse> getCallList(
		@RequestHeader("Authorization") String authorization,
		@RequestParam(value = "status", defaultValue = "pending") String status,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(required = false) LocalDate startedAt,
		@RequestParam(required = false) LocalDate endedAt,
		@RequestParam(required = false) Category category,
		@RequestParam(required = false) String keyword
	) {
		return ResponseEntity.ok(callService.findFilteredCalls(page, size, status, startedAt, endedAt, category, keyword));
	}

	@GetMapping("/{callId}")
	public ResponseEntity<?> getCallDetail(
		@PathVariable Long callId
	) {
		try {
			return ResponseEntity.ok(callService.findCall(callId));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (IllegalStateException e) {
			return ResponseEntity.status(405).body(e.getMessage());
		}
	}
}
