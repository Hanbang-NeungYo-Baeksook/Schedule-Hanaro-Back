package com.hanaro.schedule_hanaro.customer.dto.request;

import lombok.Builder;

@Builder
public record CustomerCreateRequest(String name, String phoneNum) {
}
