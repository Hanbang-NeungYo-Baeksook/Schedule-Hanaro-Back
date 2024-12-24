package com.hanaro.schedule_hanaro.customer.dto.response;

import lombok.Builder;

@Builder
public record CustomerInfoResponse(
		String name,
		String authId,
		String birth,
		String phoneNum,
		Integer callAmount,
		Integer inquiryAmount,
		Integer visitAmount
) {
	public static CustomerInfoResponse of(
		final String name,
		final String authId,
		final String birth,
		final String phoneNum,
		final Integer callAmount,
		final Integer inquiryAmount,
		final Integer visitAmount
	) {

		return CustomerInfoResponse.builder()
			.name(name)
			.authId(authId)
			.birth(birth)
			.phoneNum(phoneNum)
			.callAmount(callAmount)
			.inquiryAmount(inquiryAmount)
			.visitAmount(visitAmount)
			.build();
	}
}
