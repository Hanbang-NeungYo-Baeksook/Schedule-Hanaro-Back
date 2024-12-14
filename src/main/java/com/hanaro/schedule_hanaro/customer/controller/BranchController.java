package com.hanaro.schedule_hanaro.customer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.customer.dto.request.BranchListCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchListResponse;
import com.hanaro.schedule_hanaro.customer.service.BranchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/branches")
public class BranchController {
	private final BranchService branchService;

	@GetMapping("/{branchId}")
	public ResponseEntity<BranchDetailResponse> getBranchDetail(@PathVariable Long branchId) {
		return ResponseEntity.ok().body(branchService.findBranchById(branchId));
	}

	@GetMapping("/list")
	public ResponseEntity<BranchListResponse> getBranchList() {
		return ResponseEntity.ok().body(branchService.listBranch());
	}

	@PostMapping("/create")
	public ResponseEntity<String> createBranches(@RequestBody BranchListCreateRequest branchList) {
		return ResponseEntity.ok(branchService.saveBranchList(branchList));
	}
}
