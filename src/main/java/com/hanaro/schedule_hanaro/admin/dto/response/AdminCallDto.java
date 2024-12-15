package com.hanaro.schedule_hanaro.admin.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record AdminCallDto(
        @JsonProperty("call_id")
        Long callId,

        @JsonProperty("call_date")
        LocalDateTime callDate,

        @JsonProperty("call_num")
        int callNum,

        @JsonProperty("category")
        String category,

        @JsonProperty("status")
        String status,

        @JsonProperty("content")
        String content,

        @JsonProperty("started_at")
        LocalDateTime startedAt,

        @JsonProperty("ended_at")
        LocalDateTime endedAt
) {
    public static AdminCallDto of(
            @JsonProperty("call_id") final Long callId,
            @JsonProperty("call_date") final LocalDateTime callDate,
            @JsonProperty("call_num") final int callNum,
            @JsonProperty("category") final String category,
            @JsonProperty("status") final String status,
            @JsonProperty("content") final String content,
            @JsonProperty("started_at") final LocalDateTime startedAt,
            @JsonProperty("ended_at") final LocalDateTime endedAt
    ) {
        return AdminCallDto.builder()
                .callId(callId)
                .callDate(callDate)
                .callNum(callNum)
                .category(category)
                .status(status)
                .content(content)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();
    }
}
