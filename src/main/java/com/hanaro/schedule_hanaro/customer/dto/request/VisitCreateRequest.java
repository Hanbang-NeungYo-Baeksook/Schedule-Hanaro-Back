package com.hanaro.schedule_hanaro.customer.dto.request;

import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;

import lombok.Builder;

@Builder
public record VisitCreateRequest(
	Long customerId,
	Long branchId,
	String content,
	SectionType sectionType
) {

}
