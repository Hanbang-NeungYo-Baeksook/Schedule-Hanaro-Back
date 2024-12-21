package com.hanaro.schedule_hanaro.customer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.customer.dto.request.BranchListCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchListResponse;
import com.hanaro.schedule_hanaro.customer.service.BranchService;
import com.hanaro.schedule_hanaro.customer.dto.response.BankInfoDto;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchRecommendationResponse;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Branch", description = "영업점 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/branches")
public class BranchController {
	private final BranchService branchService;

	@Operation(summary = "영업점 상세 정보 조회", description = "특정 영업점의 지점정보와 대기 현황 정보를 조회합니다.")
	@GetMapping("/{branch-id}")
	public ResponseEntity<BranchDetailResponse> getBranchDetail(@PathVariable("branch-id") Long branchId) {
		return ResponseEntity.ok().body(branchService.findBranchById(branchId));
	}

	@Operation(summary = "영업점 정보 목록 조회", description = "영업점의 목록을 조회합니다.")
	@GetMapping("/list")
	public ResponseEntity<BranchListResponse> getBranchList(@RequestParam("latitude") double latitude,        // 사용자 위도
		@RequestParam("longitude") double longitude) {
		return ResponseEntity.ok().body(branchService.listBranch(latitude, longitude));
	}

	// 형석이 테스트용...
	@PostMapping("/create")
	public ResponseEntity<String> createBranches(@RequestBody BranchListCreateRequest branchList) {
		return ResponseEntity.ok(branchService.saveBranchList(branchList));
	}

	@Operation(summary = "추천 영업점 목록 조회", description = "추천 영업점의 목록을 조회합니다.")
	@GetMapping("/recommend")
	public ResponseEntity<List<BranchRecommendationResponse>> recommendBranches(
		@RequestParam("latitude") double latitude,        // 사용자 위도
		@RequestParam("longitude") double longitude,      // 사용자 경도
		@RequestParam("transportType") String transportType, // 이동 방식 (도보/차량)
		@RequestParam("category") SectionType category         // 카테고리 (예금/개인대출/기업대출)
	) {

		List<BranchRecommendationResponse> response = branchService.recommendBranches(latitude, longitude, transportType, category);

		return ResponseEntity.ok(response);
	}

}
