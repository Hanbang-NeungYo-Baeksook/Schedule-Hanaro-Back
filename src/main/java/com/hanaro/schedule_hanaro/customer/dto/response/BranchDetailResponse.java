package com.hanaro.schedule_hanaro.customer.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record BranchDetailResponse(
	@JsonProperty("branch_id")
	Long id,
	@JsonProperty("branch_name")
	String branchName,
	@JsonProperty("x_position")
	String xPosition,
	@JsonProperty("y_position")
	String yPosition,
	String address,
	String tel,
	@JsonProperty("business_hours")
	String businessHours,
	@JsonProperty("branch_type")
	String branchType,

	@JsonProperty("reserved")
	boolean reserved,

	@JsonProperty("visit_id")
	Long visitId,

	@JsonProperty("section_types")
	List<String> sectionTypes,

	@JsonProperty("wait_amount")
	List<Integer> waitAmount,

	@JsonProperty("wait_time")
	List<Integer> waitTime,

	@JsonProperty("distance")
	int distance
) {
	public static BranchDetailResponse of(Long id, String branchName, String xPosition, String yPosition,
		String address) {

		return BranchDetailResponse.builder()
			.id(id)
			.branchName(branchName)
			.xPosition(xPosition)
			.yPosition(yPosition)
			.address(address)
			.branchType("영업점")
			.build();
	}
}
