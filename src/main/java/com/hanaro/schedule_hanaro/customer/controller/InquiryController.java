package com.hanaro.schedule_hanaro.customer.controller;

import com.hanaro.schedule_hanaro.customer.dto.request.InquiryCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.request.InquiryListRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryCreateResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryReplyDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.customer.service.InquiryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {
	private final InquiryService inquiryService;

	// 1:1 상담 예약
	@PostMapping
	public ResponseEntity<InquiryCreateResponse> createInquiry(
		@RequestParam Long customerId, @RequestBody InquiryCreateRequest request) {
		InquiryCreateResponse response = inquiryService.createInquiry(customerId, request);
		return ResponseEntity.status(201).body(response);
	}

	// 1:1 상담 목록
	@GetMapping
	public ResponseEntity<InquiryListResponse> getInquiries(
		@RequestParam(defaultValue = "pending") String status,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "5") int size) {
		InquiryListResponse response = inquiryService.getInquiryList(status, page, size);
		return ResponseEntity.ok(response);
	}

	// 1:1 상담 상세
	@GetMapping("/{inquiry-id}")
	public ResponseEntity<InquiryResponse> getInquiryDetail(@PathVariable("inquiry-id") Long inquiryId) {
		InquiryResponse response = inquiryService.getInquiryDetail(inquiryId);
		return ResponseEntity.ok(response);
	}

	// 1:1 상담 답변 상세
	@GetMapping("/{inquiry-id}/reply")
	public ResponseEntity<InquiryReplyDetailResponse> getInquiryReplyDetail(@PathVariable("inquiry-id") Long inquiryId) {
		InquiryReplyDetailResponse response = inquiryService.getInquiryReplyDetail(inquiryId);
		return ResponseEntity.ok(response);
	}

	// 1:1 상담 취소
	@DeleteMapping("/{inquiry-id}")
	public ResponseEntity<String> cancelInquiry(@PathVariable("inquiry-id") Long inquiryId) {
		inquiryService.cancelInquiry(inquiryId);
		return ResponseEntity.ok("1:1 상담 데이터가 성공적으로 삭제되었습니다.");
	}
}
