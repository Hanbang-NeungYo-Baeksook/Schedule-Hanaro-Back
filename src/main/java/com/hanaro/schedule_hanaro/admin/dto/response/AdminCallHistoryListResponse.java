package com.hanaro.schedule_hanaro.admin.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.customer.dto.response.CallListResponse;

import lombok.Builder;

@Builder
public record AdminCallHistoryListResponse(
	int totalItems,
	List<AdminCallHistoryResponse> data,
	Pagination pagination
) {
	@Builder
	public record Pagination(
		@JsonProperty("currentPage") int currentPage,
		@JsonProperty("pageSize") int pageSize,
		@JsonProperty("hasNext") boolean hasNext
	) {}
}
