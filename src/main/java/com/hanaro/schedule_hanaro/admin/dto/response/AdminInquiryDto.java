package com.hanaro.schedule_hanaro.admin.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record AdminInquiryDto(
        @JsonProperty("inquiry_id")
        Long inquiryId,

        @JsonProperty("content")
        String content,

        @JsonProperty("category")
        String category,

        @JsonProperty("status")
        String status,

        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
    public static AdminInquiryDto of(
            @JsonProperty("inquiry_id") final Long inquiryId,
            @JsonProperty("content") final String content,
            @JsonProperty("category") final String category,
            @JsonProperty("status") final String status,
            @JsonProperty("created_at") final LocalDateTime createdAt
    ) {
        return AdminInquiryDto.builder()
                .inquiryId(inquiryId)
                .content(content)
                .category(category)
                .status(status)
                .createdAt(createdAt)
                .build();
    }
}
