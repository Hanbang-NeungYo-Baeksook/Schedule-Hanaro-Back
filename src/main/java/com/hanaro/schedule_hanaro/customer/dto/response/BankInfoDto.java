package com.hanaro.schedule_hanaro.customer.dto.response;

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

	@JsonProperty("sc1_wait_amount")
	Integer sc1WaitAmount,

	@JsonProperty("sc1_wait_time")
	Integer sc1WaitTime,

	@JsonProperty("sc2_wait_amount")
	Integer sc2WaitAmount,

	@JsonProperty("sc2_wait_time")
	Integer sc2WaitTime,

	@JsonProperty("sc3_wait_amount")
	Integer sc3WaitAmount,

	@JsonProperty("sc3_wait_time")
	Integer sc3WaitTime
	) {
	public static BankInfoDto of(Long id, String branchName, String xPosition, String yPosition, String address,
		Integer sc1WaitAmount, Integer sc1WaitTime, Integer sc2WaitAmount, Integer sc2WaitTime,
		Integer sc3WaitAmount, Integer sc3WaitTime) {

		return BankInfoDto.builder()
			.id(id)
			.branchName(branchName)
			.xPosition(xPosition)
			.yPosition(yPosition)
			.address(address)
			.branchType("영업점")
			.sc1WaitAmount(sc1WaitAmount)
			.sc1WaitTime(sc1WaitTime)
			.sc2WaitAmount(sc2WaitAmount)
			.sc2WaitTime(sc2WaitTime)
			.sc3WaitAmount(sc3WaitAmount)
			.sc3WaitTime(sc3WaitTime)
			.build();
	}
}
