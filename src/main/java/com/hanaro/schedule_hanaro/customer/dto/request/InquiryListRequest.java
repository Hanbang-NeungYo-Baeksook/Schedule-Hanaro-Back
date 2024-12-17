package com.hanaro.schedule_hanaro.customer.dto.request;

import lombok.Builder;

@Builder
public record InquiryListRequest(
	String status,
	Integer page,
	Integer size
) {
}
