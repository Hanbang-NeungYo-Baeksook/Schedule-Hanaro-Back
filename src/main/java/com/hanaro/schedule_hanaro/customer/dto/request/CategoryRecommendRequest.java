package com.hanaro.schedule_hanaro.customer.dto.request;

import lombok.Builder;

@Builder
public record CategoryRecommendRequest(
    String content
) {
} 