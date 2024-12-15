package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record VisitListResponse(
	@JsonProperty("visit_id")
	Long visitId,

	@JsonProperty("branch_name")
	String branchName,

	@JsonProperty("visit_num")
	int visitNum,

	@JsonProperty("waiting_amount")
	int waitingAmount,

	@JsonProperty("waiting_time")
	int waitingTime
) {
	public static VisitListResponse of(
		Long visitId,
		String branchName,
		int visitNum,
		int waitingAmount,
		int waitingTime
	) {
		return VisitListResponse
			.builder()
			.visitId(visitId)
			.branchName(branchName)
			.visitNum(visitNum)
			.waitingAmount(waitingAmount)
			.waitingTime(waitingTime)
			.build();
	}
}
