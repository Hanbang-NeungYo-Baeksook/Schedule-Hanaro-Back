package com.hanaro.schedule_hanaro.customer.dto.request;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;

import lombok.Builder;

@Builder
public record VisitCreateRequest(
	Long branchId,
	String content,
	Category category
) {

}
