package com.hanaro.schedule_hanaro.customer.dto.response;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import lombok.Builder;

@Builder
public record CategoryRecommendResponse(
    String categoryCode,
    String categoryName,
    double confidence
) {
    public static CategoryRecommendResponse from(Category category, double confidence) {
        return CategoryRecommendResponse.builder()
            .categoryCode(category.name())
            .categoryName(category.getCategory())
            .confidence(confidence)
            .build();
    }
} 