package com.hanaro.schedule_hanaro.customer.controller;

import com.hanaro.schedule_hanaro.customer.dto.request.InquiryListRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryListResponse;
import com.hanaro.schedule_hanaro.customer.service.InquiryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {
	private final InquiryService inquiryService;

	@GetMapping
	public ResponseEntity<InquiryListResponse> getInquiryList(
		@RequestParam String status,
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false) Integer size
	) {
		InquiryListRequest request = InquiryListRequest.of(status, page, size);
		return ResponseEntity.ok().body(inquiryService.getInquiries(request));
		}
}
