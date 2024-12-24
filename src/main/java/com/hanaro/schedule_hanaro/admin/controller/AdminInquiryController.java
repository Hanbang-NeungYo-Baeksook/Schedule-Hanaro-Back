package com.hanaro.schedule_hanaro.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.admin.dto.request.AdminInquiryListRequest;
import com.hanaro.schedule_hanaro.admin.dto.request.AdminInquiryResponseRequest;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryDetailResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryListResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminInquiryService;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin-Inquiry", description = "관리자 1:1 상담 API")
@RestController
@RequestMapping("/admin/api/inquiries")
@RequiredArgsConstructor
public class AdminInquiryController {
	private final AdminInquiryService adminInquiryService;

	@Operation(summary = "1:1 상담 목록 조회", description = "1:1 상담 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<?> getInquiryList(
		@RequestParam(value = "status", required = false, defaultValue = "PENDING") InquiryStatus inquiryStatus,
		@RequestParam(required = false) Category category,
		@RequestParam(value = "search_content", required = false) String searchContent,
		@RequestParam(defaultValue = "1") Integer page,
		@RequestParam(defaultValue = "5") Integer size
	) {
		Category enumCategory = (category != null) ? category : Category.LOAN;

		AdminInquiryListRequest request = AdminInquiryListRequest.from(
			inquiryStatus, enumCategory, searchContent, page, size
		);

		AdminInquiryListResponse response = adminInquiryService.findInquiryList(request);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "1:1 상담 상세 조회", description = "특정 1:1 상담의 상세 정보를 조회합니다.")
	@GetMapping("/{inquiry-id}")
	public ResponseEntity<AdminInquiryDetailResponse> getInquiryDetail(
		@PathVariable("inquiry-id") Long inquiryId
	) {
		AdminInquiryDetailResponse response = adminInquiryService.findInquiryDetail(inquiryId);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "1:1 상담 답변 등록", description = "특정 1:1 상담에 대해 답변을 등록합니다.")
	@PostMapping("/register/{inquiry-id}")
	public ResponseEntity<AdminInquiryResponse> registerInquiryResponse(
		@PathVariable("inquiry-id") Long inquiryId,
		@RequestBody AdminInquiryResponseRequest request,
		Authentication authentication
	) {
		AdminInquiryResponse response = adminInquiryService.registerInquiryResponse(inquiryId, request.content(),
			authentication);
		return ResponseEntity.status(200).body(response);
	}
}
