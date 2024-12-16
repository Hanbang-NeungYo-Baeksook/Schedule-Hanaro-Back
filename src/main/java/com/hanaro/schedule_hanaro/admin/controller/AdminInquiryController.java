package com.hanaro.schedule_hanaro.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.admin.dto.request.AdminInquiryListRequest;
import com.hanaro.schedule_hanaro.admin.dto.request.AdminInquiryResponseRequest;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerListResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryDetailResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryListResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminCustomerService;
import com.hanaro.schedule_hanaro.admin.service.AdminInquiryService;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/admin/api/inquiries")
@RequiredArgsConstructor
public class AdminInquiryController {
	private final AdminInquiryService adminInquiryService;
	private final AdminCustomerService adminCustomerService;

	@GetMapping
	public ResponseEntity<AdminInquiryListResponse> getInquiryList(
		@RequestParam(required = false) String status,
		@RequestParam(required = false) Category category,
		@RequestParam(required = false) String searchContent,
		@RequestParam(defaultValue = "0") Integer page,
		@RequestParam(defaultValue = "5") Integer size
	) {
		AdminInquiryListRequest request = AdminInquiryListRequest.from(status, category,searchContent,page,size);
		AdminInquiryListResponse response = adminInquiryService.findInquiryList(request);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/{inquiry-id}")
	public ResponseEntity<AdminInquiryDetailResponse> getInquiryDetail(
		@PathVariable("inquiry-id") Long inquiryId
	) {
		AdminInquiryDetailResponse response = adminInquiryService.findInquiryDetail(inquiryId);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/register/{inquiry-id}")
	public ResponseEntity<AdminInquiryResponse> registerInquiryResponse(
		@PathVariable("inquiry-id") Long inquiryId,
		@RequestBody AdminInquiryResponseRequest request
	) {
		AdminInquiryResponse response = adminInquiryService.registerInquiryResponse(inquiryId, request);
		return ResponseEntity.status(201).body(response);
	}

}
