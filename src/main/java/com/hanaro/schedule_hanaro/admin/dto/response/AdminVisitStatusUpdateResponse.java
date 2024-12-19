package com.hanaro.schedule_hanaro.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AdminVisitStatusUpdateResponse(
        @JsonProperty("previous_num") int previousNum,
        @JsonProperty("current_num") int currentNum,
        @JsonProperty("next_num") int nextNum,
        @JsonProperty("section_info") SectionInfo sectionInfo
) {
    @Builder
    public record SectionInfo(
            @JsonProperty("section_id") Long sectionId,
            @JsonProperty("section_type") String sectionType,
            @JsonProperty("current_num") int currentNum,
            @JsonProperty("wait_amount") int waitAmount,
            @JsonProperty("wait_time") int waitTime,
            @JsonProperty("today_visitors") int todayVisitors
    ) {}
}
