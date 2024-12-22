package com.hanaro.schedule_hanaro.customer.dto.response;

import lombok.Builder;

@Builder
public record CustomerInfoResponse(
		String name,
		String authId,
		String birth,
		String phoneNum,
		Integer callAmount,
		Integer InquiryAmount
) {
	public static CustomerInfoResponse of(
		final String name,
		final String authId,
		final String birth,
		final String phoneNum,
		final Integer callAmount,
		final Integer InquiryAmount
	) {

		return CustomerInfoResponse.builder()
			.name(name)
			.authId(authId)
			.birth(birth)
			.phoneNum(phoneNum)
			.callAmount(callAmount)
			.InquiryAmount(InquiryAmount)
			.build();
	}
}
