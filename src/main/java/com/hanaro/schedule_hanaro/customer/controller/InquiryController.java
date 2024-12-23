package com.hanaro.schedule_hanaro.customer.controller;

import java.security.Principal;

import com.hanaro.schedule_hanaro.customer.dto.request.InquiryCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.request.InquiryListRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.ErrorResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryCreateResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryReplyDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.customer.service.InquiryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Inquiry", description = "1:1 상담 API")
@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {
	private final InquiryService inquiryService;

	// 1:1 상담 예약
	@Operation(summary = "1:1 상담 예약 생성", description = "새로운 1:1 상담 예약을 생성합니다.")
	@PostMapping
	public ResponseEntity<?> createInquiry(
		@RequestBody InquiryCreateRequest request,
		Authentication authentication
	) {
		try {
			InquiryCreateResponse response = inquiryService.createInquiry(authentication, request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (NumberFormatException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse("400", "Invalid customer ID format."));
		} catch (RuntimeException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse("401", e.getMessage()));
		}
	}

	// 1:1 상담 목록
	@Operation(summary = "1:1 상담 목록 조회", description = "1:1 상담 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<?> getInquiries(
		@RequestParam(defaultValue = "pending") String status,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "5") int size) {
		status = status.toUpperCase();
		InquiryListResponse response = inquiryService.getInquiryList(status, page, size);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	// 1:1 상담 상세
	@Operation(summary = "1:1 상담 상세 조회", description = "특정 1:1 상담 상세 정보를 조회합니다.")
	@GetMapping("/{inquiry-id}")
	public ResponseEntity<?> getInquiryDetail(@PathVariable("inquiry-id") Long inquiryId) {
		InquiryResponse response = inquiryService.getInquiryDetail(inquiryId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	// 1:1 상담 답변 상세
	@Operation(summary = "1:1 상담 답변 상세", description = "특정 1:1 상담의 답변 상세 내용을 조회합니다.")
	@GetMapping("/{inquiry-id}/reply")
	public ResponseEntity<?> getInquiryReplyDetail(@PathVariable("inquiry-id") Long inquiryId) {
		InquiryReplyDetailResponse response = inquiryService.getInquiryReplyDetail(inquiryId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	// 1:1 상담 취소
	@Operation(summary = "1:1 상담 예약 취소", description = "특정 1:1 상담 예약을 취소합니다.")
	@DeleteMapping("/{inquiry-id}")
	public ResponseEntity<?> cancelInquiry(@PathVariable("inquiry-id") Long inquiryId) {
		inquiryService.cancelInquiry(inquiryId);
		return ResponseEntity.status(HttpStatus.OK).body(inquiryId);
	}
}
