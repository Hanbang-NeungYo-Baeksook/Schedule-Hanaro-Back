package com.hanaro.schedule_hanaro.customer.dto.response;

import com.hanaro.schedule_hanaro.global.domain.Branch;

import lombok.Builder;

@Builder
public record BranchDetailResponse(
	String branchNum,
	String branchName,
	String branchType,
	String xPosition,
	String yPosition,
	String address,
	String tel,
	String businessTime
) {
	public static BranchDetailResponse from(
		Branch branch
	){
		return BranchDetailResponse.builder()
			.branchNum(branch.getBranchNum())
			.branchName(branch.getName())
			.branchType(branch.getType())
			.xPosition(branch.getXPosition())
			.yPosition(branch.getYPosition())
			.address(branch.getAddress())
			.tel(branch.getTel())
			.businessTime(branch.getBusinessTime())
			.build();
	}
}
