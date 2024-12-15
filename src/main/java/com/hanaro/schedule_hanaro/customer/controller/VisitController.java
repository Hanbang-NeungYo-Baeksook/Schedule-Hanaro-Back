package com.hanaro.schedule_hanaro.customer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.customer.dto.request.VisitCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.VisitDetailResponse;
import com.hanaro.schedule_hanaro.customer.service.VisitService;

@RestController
@RequestMapping("/api/visits")
public class VisitController {
	private final VisitService visitService;

	public VisitController(VisitService visitService) {
		this.visitService = visitService;
	}

	@GetMapping
	public String getVisitList() {
		return "test";
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
