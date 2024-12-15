package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record InquiryCreateResponse(
	@JsonProperty("inquiry_id")
	Long inquiryId
) {
}
