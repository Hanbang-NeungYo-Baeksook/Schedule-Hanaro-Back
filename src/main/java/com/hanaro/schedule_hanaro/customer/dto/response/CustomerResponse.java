package com.hanaro.schedule_hanaro.customer.dto.response;

import com.hanaro.schedule_hanaro.global.domain.Customer;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record CustomerResponse(String customerName, String phoneNum) {
	public static CustomerResponse from(Customer customer){
		return CustomerResponse.builder()
			.customerName(customer.getName())
			.phoneNum(customer.getPhoneNum())
			.build();
	}
}
