package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record AtmInfoDto(
	@JsonProperty("branch_id")
	Long id,
	@JsonProperty("branch_name")
	String branchName,
	@JsonProperty("x_position")
	String xPosition,
	@JsonProperty("y_position")
	String yPosition,
	String address,
	@JsonProperty("business_hours")
	String businessHours,
	@JsonProperty("branch_type")
	String branchType
) {
	public static AtmInfoDto of(
		Long id,
		String branchName,
		String xPosition,
		String yPosition,
		String address,
		String businessHours,
		String branchType
	) {
		return AtmInfoDto.builder()
			.id(id)
			.branchName(branchName)
			.xPosition(xPosition)
			.yPosition(yPosition)
			.address(address)
			.businessHours(businessHours)
			.branchType(branchType)
			.build();
	}
}
