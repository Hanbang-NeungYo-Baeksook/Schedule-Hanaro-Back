package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	@JsonProperty("current_num")
	int currentNum,
	@JsonProperty("total_num")
	int totalNum
) {
	public static BankInfoDto of(Long id, String branchName, String xPosition, String yPosition, String address,
		String branchType, int currentNum, int totalNum) {
		return BankInfoDto.builder()
			.id(id)
			.branchName(branchName)
			.xPosition(xPosition)
			.yPosition(yPosition)
			.address(address)
			.branchType(branchType)
			.currentNum(currentNum)
			.totalNum(totalNum)
			.build();
	}
}
