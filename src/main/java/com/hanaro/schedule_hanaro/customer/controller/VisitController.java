package com.hanaro.schedule_hanaro.customer.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.customer.dto.request.VisitCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.CreateVisitResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.DeleteVisitResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.VisitDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.VisitListResponse;
import com.hanaro.schedule_hanaro.customer.service.VisitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Visit", description = "방문 상담 API")
@RestController
@RequestMapping("/api/visits")
public class VisitController {
	private final VisitService visitService;

	public VisitController(VisitService visitService) {
		this.visitService = visitService;
	}

	@Operation(summary = "방문 상담 목록 조회", description = "방문 상담 목록을 조회합니다.")
	@GetMapping
	public ResponseEntity<VisitListResponse> getVisitList(
		Authentication authentication,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return ResponseEntity.ok(visitService.getVisitList(authentication, page, size));
	}

	@Operation(summary = "방문 상담 상세 조회", description = "특정 방문 상담의 정보를 조회합니다.")
	@GetMapping("/{visit-id}")
	public ResponseEntity<VisitDetailResponse> getVisit(@PathVariable("visit-id") Long visitId) {
		return ResponseEntity.ok(visitService.getVisitDetail(visitId));
	}

	@Operation
	@DeleteMapping("/{visit-id}")
	public ResponseEntity<DeleteVisitResponse> deleteVisit(@PathVariable("visit-id") Long visitId) throws InterruptedException {
		return ResponseEntity.ok().body(visitService.deleteVisitReservation(visitId));
	}

	@Operation(summary = "방문 상담 예약 생성", description = "새로운 방문 상담 예약을 생성합니다.")
	@PostMapping
	public ResponseEntity<CreateVisitResponse> addVisit(
		@RequestBody VisitCreateRequest visitReservationCreateRequest,
		Authentication authentication
	) throws InterruptedException {
		System.out.println("visitReservationCreateRequest = " + visitReservationCreateRequest);
		return ResponseEntity.ok(visitService.addVisitReservation(visitReservationCreateRequest, authentication));
	}
}
