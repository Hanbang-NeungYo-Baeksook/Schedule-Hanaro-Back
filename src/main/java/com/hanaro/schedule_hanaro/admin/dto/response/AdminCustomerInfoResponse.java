package com.hanaro.schedule_hanaro.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.Customer;

import lombok.Builder;

@Builder
public record AdminCustomerInfoResponse(
        @JsonProperty("customer_id")
        Long customerId,

        String name,

        @JsonProperty("auth_id")
        String authId,

        String phone,

        String birth
) {
        public static AdminCustomerInfoResponse from(Customer customer) {
                return AdminCustomerInfoResponse.builder()
                        .customerId(customer.getId())
                        .name(customer.getName())
                        .authId(customer.getAuthId())
                        .phone(customer.getPhoneNum())
                        .birth(String.valueOf(customer.getBirth()))
                        .build();
        }
}
