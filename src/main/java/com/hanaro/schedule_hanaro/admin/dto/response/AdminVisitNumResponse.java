package com.hanaro.schedule_hanaro.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;


@Builder
public record AdminVisitNumResponse(
        @JsonProperty("carousel")
        AdminVisitCarouselDto carousel,

        @JsonProperty("statistics")
        AdminVisitStatisticsDto statistics
) {
    public static AdminVisitNumResponse of(
            final AdminVisitCarouselDto carousel,
            final AdminVisitStatisticsDto statistics
    ) {
        return AdminVisitNumResponse.builder()
                .carousel(carousel)
                .statistics(statistics)
                .build();
    }

}
