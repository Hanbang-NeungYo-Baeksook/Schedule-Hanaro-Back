package com.hanaro.schedule_hanaro.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record AdminCustomerInquiryListResponse(
        @JsonProperty("phone_inquiries")
        List<AdminCallDto> phoneInquiries,

        @JsonProperty("one_to_one_inquiries")
        List<AdminInquiryDto> oneToOneInquiries
) {
    public static AdminCustomerInquiryListResponse of(
            @JsonProperty("phone_inquiries") final List<AdminCallDto> phoneInquiries,
            @JsonProperty("one_to_one_inquiries") final List<AdminInquiryDto> oneToOneInquiries
    ) {
        return AdminCustomerInquiryListResponse.builder()
                .phoneInquiries(phoneInquiries)
                .oneToOneInquiries(oneToOneInquiries)
                .build();
    }
}
