package com.hanaro.schedule_hanaro.customer.dto.response;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record CustomerResponse(String customerName, String phoneNum) {
	public static CustomerResponse from(String customerName, String phoneNum){
		return CustomerResponse.builder()
			.customerName(customerName)
			.phoneNum(phoneNum)
			.build();
	}
}
