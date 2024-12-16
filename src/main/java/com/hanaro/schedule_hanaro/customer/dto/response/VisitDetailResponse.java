package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record VisitDetailResponse(
	@JsonProperty("visit_id")
	Long visitId,

	@JsonProperty("branch_name")
	String branchName,

	@JsonProperty("visit_num")
	int visitNum,

	@JsonProperty("current_num")
	int currentNum,

	@JsonProperty("waiting_amount")
	int waitingAmount,

	@JsonProperty("waiting_time")
	int waitingTime
) {
	public static VisitDetailResponse of(
		Long visitId,
		String branchName,
		int visitNum,
		int currentNum,
		int waitingAmount,
		int waitingTime
	) {
		return VisitDetailResponse
			.builder()
			.visitId(visitId)
			.branchName(branchName)
			.visitNum(visitNum)
			.currentNum(currentNum)
			.waitingAmount(waitingAmount)
			.waitingTime(waitingTime)
			.build();
	}
}
