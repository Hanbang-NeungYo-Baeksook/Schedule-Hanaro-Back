package com.hanaro.schedule_hanaro.branch.dto.response;

import lombok.Builder;

@Builder
public record BranchResponse(
	Long branchNum,
	String branchName,
	int branchType,
	String xPosition,
	String yPosition,
	String address,
	String tel,
	String businessTime
) {
	public static BranchResponse from(
		Long branchNum,
		String branchName,
		int branchType,
		String xPosition,
		String yPosition,
		String address,
		String tel,
		String businessTime
	){
		return BranchResponse.builder()
			.branchNum(branchNum)
			.branchName(branchName)
			.branchType(branchType)
			.xPosition(xPosition)
			.yPosition(yPosition)
			.address(address)
			.tel(tel)
			.businessTime(businessTime)
			.build();
	}
}
