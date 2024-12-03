package com.hanaro.schedule_hanaro.customer.dto.response;

import java.util.List;

import com.hanaro.schedule_hanaro.global.domain.Branch;

public record AllBranchResponse(
	List<BranchDetailResponse> branchDetailResponseList
) {
	public static AllBranchResponse from(List <Branch> branchList){
		return new AllBranchResponse(branchList.stream().map(BranchDetailResponse::from).toList());
	}
}
