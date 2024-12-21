package com.hanaro.schedule_hanaro.customer.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import lombok.Builder;

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
		String status,
		@JsonProperty("wait_num") int waitNum,
		@JsonProperty("estimated_wait_time") int estimatedWaitTime
	) {
		public static CallData of(Long callId, String callDate, String callTime, int callNum,
			Category category, Status status, int waitNum, int estimatedWaitTime) {
			return CallData.builder()
				.callId(callId)
				.callDate(callDate)
				.callTime(callTime)
				.callNum(callNum)
				.category(category.toString())
				.status(status.toString())
				.waitNum(waitNum)
				.estimatedWaitTime(estimatedWaitTime)
				.build();
		}
	}

	@Builder
	public record Pagination(
		@JsonProperty("currentPage") int currentPage,
		@JsonProperty("pageSize") int pageSize,
		@JsonProperty("hasNext") boolean hasNext
	) {
	}
}
