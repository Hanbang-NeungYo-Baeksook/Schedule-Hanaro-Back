package com.hanaro.schedule_hanaro.branch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.branch.dto.response.BranchResponse;
import com.hanaro.schedule_hanaro.branch.service.BranchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class BranchController {
	private final BranchService branchService;

	@GetMapping("/branch")
	public ResponseEntity<BranchResponse> findBranch(@RequestParam Long branchId){
		return ResponseEntity.ok().body(branchService.findByBranchId(branchId));
	}
}
