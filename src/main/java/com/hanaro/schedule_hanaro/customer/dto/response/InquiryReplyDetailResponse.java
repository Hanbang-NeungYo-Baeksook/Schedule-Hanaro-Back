package com.hanaro.schedule_hanaro.customer.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record InquiryReplyDetailResponse(
	String content,
	String status,
	String reply,
	List<String> tag
) {
}
