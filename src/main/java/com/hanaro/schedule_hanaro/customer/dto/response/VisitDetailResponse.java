package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record VisitDetailResponse(
	@JsonProperty("visit_id")
	Long visitId,

	@JsonProperty("branch_id")
	Long branchId,

	@JsonProperty("branch_name")
	String branchName,

	@JsonProperty("x_position")
	String xPosition,

	@JsonProperty("y_position")
	String yPosition,

	@JsonProperty("section_type")
	String sectionType,

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
		Long branchId,
		String branchName,
		String xPosition,
		String yPosition,
		String sectionType,
		int visitNum,
		int currentNum,
		int waitingAmount,
		int waitingTime
	) {
		return VisitDetailResponse
			.builder()
			.visitId(visitId)
			.branchId(branchId)
			.branchName(branchName)
			.xPosition(xPosition)
			.yPosition(yPosition)
			.sectionType(sectionType)
			.visitNum(visitNum)
			.currentNum(currentNum)
			.waitingAmount(waitingAmount)
			.waitingTime(waitingTime)
			.build();
	}
}
