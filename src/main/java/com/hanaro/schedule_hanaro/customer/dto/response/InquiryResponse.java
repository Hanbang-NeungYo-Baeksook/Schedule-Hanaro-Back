package com.hanaro.schedule_hanaro.customer.dto.response;

import lombok.Builder;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Builder
public record InquiryResponse(
	@JsonProperty("inquiry_id") Long inquiryId,
	@JsonProperty("inquiry_num") int inquiryNum,
	@JsonProperty("customer_name") String customerName,
	String category,
	String status,
	String content,
	List<String> tags
) {
}
