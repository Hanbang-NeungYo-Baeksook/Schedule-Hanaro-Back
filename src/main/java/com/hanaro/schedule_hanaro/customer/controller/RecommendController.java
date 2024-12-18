package com.hanaro.schedule_hanaro.customer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.customer.dto.request.RecommendCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.RecommendListResponse;
import com.hanaro.schedule_hanaro.global.service.RecommendService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Recommend", description = "추천 답변 API")
@RestController
@RequestMapping("/api/recommends")
public class RecommendController {
	final RecommendService recommendService;

	public RecommendController(RecommendService recommendService) {
		this.recommendService = recommendService;
	}

	@Operation(summary = "추천 답변 제공", description = "문의 등록 전, 작성한 문의 내용을 바탕으로 추천 답변을 제공합니다.")
	@PostMapping
	public ResponseEntity<RecommendListResponse> getTop3Recommends(
		@RequestBody RecommendCreateRequest recommendCreateRequest) {
		return ResponseEntity.ok(recommendService.getRecommends(recommendCreateRequest.query()));
	}
}
