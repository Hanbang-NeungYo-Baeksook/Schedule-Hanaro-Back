package com.hanaro.schedule_hanaro.customer.dto.request;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import lombok.Builder;

@Builder
public record InquiryCreateRequest(
	Category category,
	String content
) {
}
