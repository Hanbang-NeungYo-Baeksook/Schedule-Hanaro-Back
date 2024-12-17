package com.hanaro.schedule_hanaro.customer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.customer.dto.request.RecommendCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.RecommendListResponse;
import com.hanaro.schedule_hanaro.global.service.RecommendService;

@RestController
@RequestMapping("/api/recommends")
public class RecommendController {
	final RecommendService recommendService;

	public RecommendController(RecommendService recommendService) {
		this.recommendService = recommendService;
	}

	@PostMapping
	public ResponseEntity<RecommendListResponse> getTop3Recommends(
		@RequestBody RecommendCreateRequest recommendCreateRequest) {
		return ResponseEntity.ok(recommendService.getRecommends(recommendCreateRequest.query()));
	}
}
