package com.hanaro.schedule_hanaro.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.Visit;
import lombok.Builder;

import java.util.Collections;
import java.util.List;


@Builder
public record AdminVisitInquiryInfoResponse(
        @JsonProperty("visit_id")
        Long visitId,

        @JsonProperty("customer_id")  // JSON 필드명을 프론트엔드와 맞춤
        Long customerId,   

        String category,

        String content,

        List<String> tags
) {
    public static  AdminVisitInquiryInfoResponse from(Visit visit) {
        return  AdminVisitInquiryInfoResponse.builder()
                .visitId(visit.getId())
                .customerId(visit.getCustomer().getId()) 
                .category(visit.getCategory() != null ? visit.getCategory().getCategory() : null)
                .content(visit.getContent())
                .tags(Collections.singletonList(visit.getTags()))
                .build();
    }
}
