package com.hanaro.schedule_hanaro.customer.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record InquiryResponse(
	Long inquiryId,
	int inquiryNum,
	String category,
	String status,
	String content,
	List<String> tags
) {

}
