package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import java.util.List;

@Builder
public record CallListResponse(
	List<CallData> data,
	Pagination pagination
) {

	@Builder
	public record CallData(
		@JsonProperty("call_id") Long callId,
		@JsonProperty("call_date") String callDate,
		@JsonProperty("call_time") String callTime,
		@JsonProperty("call_num") int callNum,
		String category,
		String status
	) {}

	@Builder
	public record Pagination(
		@JsonProperty("currentPage") int currentPage,
		@JsonProperty("pageSize") int pageSize,
		@JsonProperty("hasNext") boolean hasNext
	) {}
}
