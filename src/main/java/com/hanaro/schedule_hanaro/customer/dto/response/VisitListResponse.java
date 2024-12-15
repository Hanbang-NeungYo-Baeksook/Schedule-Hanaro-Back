package com.hanaro.schedule_hanaro.customer.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record VisitListResponse(
	List<VisitData> data,
	Pagination pagination
) {

	@Builder
	public record VisitData(
		@JsonProperty("visit_id") Long visitId,
		@JsonProperty("branch_name") String branchName,
		@JsonProperty("visit_num") int visitNum,
		@JsonProperty("waiting_amount") int waitingAmount,
		@JsonProperty("waiting_time") int waitingTime
	) {
	}

	@Builder
	public record Pagination(
		@JsonProperty("currentPage") int currentPage,
		@JsonProperty("pageSize") int pageSize,
		@JsonProperty("hasNext") boolean hasNext
	) {
	}
}
