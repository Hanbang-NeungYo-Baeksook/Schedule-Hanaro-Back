package com.hanaro.schedule_hanaro.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.Customer;

import lombok.Builder;

@Builder
public record AdminCustomerInfoResponse(
        @JsonProperty("customer_id")
        Long customerId,

        @JsonProperty("customer_name")
        String name,

        @JsonProperty("auth_id")
        String authId,

        @JsonProperty("phone_number")
        String phone,

        @JsonProperty("birth_date")
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
