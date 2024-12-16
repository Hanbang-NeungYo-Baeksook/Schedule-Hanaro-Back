package com.hanaro.schedule_hanaro.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;


@Builder
public record AdminVisitStatisticsDto(
        @JsonProperty("expectedWaitingCount")
        int expectedWaitingCount,

        @JsonProperty("estimatedWaitingTime")
        int estimatedWaitingTime,

        @JsonProperty("todayVisitors")
        int todayVisitors
) {
     public static AdminVisitStatisticsDto of(
            final int expectedWaitingCount,
            final int estimatedWaitingTime,
            final int todayVisitors
    ) {
        return AdminVisitStatisticsDto.builder()
                .expectedWaitingCount(expectedWaitingCount)
                .estimatedWaitingTime(estimatedWaitingTime)
                .todayVisitors(todayVisitors)
                .build();
    }
}
