package com.hanaro.schedule_hanaro.admin.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;

import lombok.Builder;

@Builder
public record AdminCallDetailResponse(
	@JsonProperty("call_id")
	Long callId,
	String content,
	Category category,
	String tags,
	@JsonProperty("call_at")
	LocalDateTime callDate,
	@JsonProperty("started_at")
	LocalDateTime startedAt,
	@JsonProperty("ended_at")
	LocalDateTime endedAt,
	@JsonProperty("customer_name")
	String customerName,
	String mobile,
	@JsonProperty("reply_content")
	String replyContent
) {
	public static AdminCallDetailResponse from (
		final Call call,
		final Customer customer
	) {
		return AdminCallDetailResponse.builder()
			.callId(call.getId())
			.content(call.getContent())
			.category(call.getCategory())
			.tags(call.getTags())
			.callDate(call.getCallDate())
			.startedAt(call.getStartedAt())
			.endedAt(call.getEndedAt())
			.customerName(customer.getName())
			.mobile(customer.getPhoneNum())
			// .replyContent(call.get)
			.build();
	}
}
