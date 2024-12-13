package com.hanaro.schedule_hanaro.customer.dto.response;

import lombok.Builder;

@Builder
public record CustomerResponse(
		String customerName,
		String phoneNum
) {
	public static CustomerResponse from(
			final String name,
			final String phoneNum
	) {
		return CustomerResponse.builder()
			.customerName(name)
			.phoneNum(phoneNum)
			.build();
	}
}
