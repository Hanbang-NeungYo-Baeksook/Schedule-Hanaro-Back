package com.hanaro.schedule_hanaro.customer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;

import lombok.Builder;

@Builder
public record VisitCreateRequest(
	@JsonProperty("branch_id")
	Long branchId,
	String content,
	Category category
) {

}
