package com.hanaro.schedule_hanaro.customer.dto.response;

import lombok.Builder;

@Builder
public record CustomerInfoResponse(
		String customerName,
		String phoneNum
) {
	public static CustomerInfoResponse from(
			final String name,
			final String phoneNum
	) {
		return CustomerInfoResponse.builder()
			.customerName(name)
			.phoneNum(phoneNum)
			.build();
	}
}
