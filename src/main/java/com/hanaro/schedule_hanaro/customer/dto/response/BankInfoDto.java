package com.hanaro.schedule_hanaro.customer.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;

import lombok.Builder;

@Builder
public record BankInfoDto(
	@JsonProperty("branch_id")
	Long id,
	@JsonProperty("branch_name")
	String branchName,
	@JsonProperty("x_position")
	String xPosition,
	@JsonProperty("y_position")
	String yPosition,
	String address,
	@JsonProperty("branch_type")
	String branchType,

	@JsonProperty("wait_amount")
	List<Integer> waitAmount,

	@JsonProperty("wait_time")
	List<Integer> waitTime,

	@JsonProperty("distance")
	long distance
	) {
	public static BankInfoDto of(Long id, String branchName, String xPosition, String yPosition, String address) {

		return BankInfoDto.builder()
			.id(id)
			.branchName(branchName)
			.xPosition(xPosition)
			.yPosition(yPosition)
			.address(address)
			.branchType("영업점")
			.build();
	}
}
