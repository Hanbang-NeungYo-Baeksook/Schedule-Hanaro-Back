package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record BranchDetailResponse(
	@JsonProperty("branch_id")
	Long id,
	@JsonProperty("branch_name")
	String branchName,
	String address,
	String tel,
	@JsonProperty("business_hours")
	String businessHours,
	@JsonProperty("branch_type")
	String branchType,
	@JsonProperty("current_num")
	int currentNum,
	@JsonProperty("total_num")
	int totalNum,
	@JsonProperty("wait_amount")
	int waitAmount

) {
	public static BranchDetailResponse of(
		Long id,
		String branchName,
		String address,
		String tel,
		String businessHours,
		String branchType,
		int currentNum,
		int totalNum,
		int waitAmount

	) {
		return BranchDetailResponse.builder()
			.id(id)
			.branchName(branchName)
			.address(address)
			.tel(tel)
			.businessHours(businessHours)
			.branchType(branchType)
			.currentNum(currentNum)
			.totalNum(totalNum)
			.waitAmount(waitAmount)
			.build();
	}
}
