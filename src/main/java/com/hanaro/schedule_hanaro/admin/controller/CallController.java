package com.hanaro.schedule_hanaro.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.admin.dto.response.CallWaitResponse;
import com.hanaro.schedule_hanaro.admin.service.CallService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/calls")
public class CallController {
	private final CallService callService;

	@GetMapping("/wait")
	public ResponseEntity<CallWaitResponse> getCallWaitList() {
		// 전화 상담 대기 목록
		return ResponseEntity.ok(callService.findWaitList());
	}

	@PatchMapping("/{callId}")
	public ResponseEntity<String> patchCallStatus(@PathVariable Long callId) {
		// 전화 상담 상태 변경
		try {
			callService.changeCallStatus(callId);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (IllegalStateException e) {
			return ResponseEntity.status(405).body(e.getMessage());
		}
		return ResponseEntity.ok(callService.changeCallStatus(callId));
	}

	@PostMapping("/{callId}")
	public ResponseEntity<String> postCallMemo(@PathVariable Long callId, @RequestBody String content) {
		// 전화 상담 메모 등록
		return ResponseEntity.ok(callService.saveCallMemo(callId, content));
	}
}
