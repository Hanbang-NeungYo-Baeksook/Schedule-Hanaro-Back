package com.hanaro.schedule_hanaro.customer.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.customer.dto.request.VisitCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.VisitDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.VisitListResponse;
import com.hanaro.schedule_hanaro.customer.service.VisitService;

@RestController
@RequestMapping("/api/visits")
public class VisitController {
	private final VisitService visitService;

	public VisitController(VisitService visitService) {
		this.visitService = visitService;
	}

	@GetMapping
	public ResponseEntity<VisitListResponse> getVisitList(
		Authentication authentication,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return ResponseEntity.ok(visitService.getVisitList(authentication, page, size));
	}

	@GetMapping("/{visit-id}")
	public ResponseEntity<VisitDetailResponse> getVisit(@PathVariable("visit-id") Long visitId) {
		return ResponseEntity.ok(visitService.getVisitDetail(visitId));
	}

	@PostMapping
	public ResponseEntity<Long> addVisit(
		@RequestBody VisitCreateRequest visitReservationCreateRequest
	) throws InterruptedException {
		System.out.println("visitReservationCreateRequest = " + visitReservationCreateRequest);
		return ResponseEntity.ok(visitService.addVisitReservation(visitReservationCreateRequest));
	}

}
