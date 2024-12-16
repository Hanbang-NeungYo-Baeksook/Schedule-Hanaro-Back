package com.hanaro.schedule_hanaro.admin.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record AdminVisitCarouselDto(
        @JsonProperty("numbers")
        List<Integer> numbers,

        @JsonProperty("angle")
        int angle,

        @JsonProperty("displayNum")
        List<Integer> displayNum
) {
    public static AdminVisitCarouselDto of(
            final List<Integer> numbers,
            final int angle,
            final List<Integer> displayNum
    ) {
        return AdminVisitCarouselDto.builder()
                .numbers(numbers)
                .angle(angle)
                .displayNum(displayNum)
                .build();
    }
}
