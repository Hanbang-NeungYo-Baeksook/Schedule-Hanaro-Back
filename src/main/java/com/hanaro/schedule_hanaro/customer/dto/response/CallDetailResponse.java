package com.hanaro.schedule_hanaro.customer.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import lombok.Builder;
import java.util.List;

@Builder
public record CallDetailResponse(
	@JsonProperty("call_id") Long callId,
	@JsonProperty("customer_name") String customerName,
	@JsonProperty("call_date") String callDate,
	@JsonProperty("call_time") String callTime,
	@JsonProperty("call_num") int callNum,
	String category,
	String status,
	String content,
	List<String> tags,
	@JsonProperty("wait_num") int waitNum,
	@JsonProperty("estimated_wait_time") int estimatedWaitTime
) {
	public static CallDetailResponse of(
		Long callId,
		String customerName,
		String callDate,
		String callTime,
		int callNum,
		Category category,
		Status status,
		String content,
		List<String> tags,
		int waitNum,
		int estimatedWaitTime
	) {
		return CallDetailResponse.builder()
			.callId(callId)
			.customerName(customerName)
			.callDate(callDate)
			.callTime(callTime)
			.callNum(callNum)
			.category(category.toString())
			.status(status.toString())
			.content(content)
			.tags(tags)
			.waitNum(waitNum)
			.estimatedWaitTime(estimatedWaitTime)
			.build();
	}
}
