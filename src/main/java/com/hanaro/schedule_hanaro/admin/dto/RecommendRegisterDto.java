package com.hanaro.schedule_hanaro.admin.dto;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;

import lombok.Builder;

@Builder
public record RecommendRegisterDto(
	String query,
	String response,
	Category category,
	String queryVector
) {
	public static RecommendRegisterDto of(
		String query,
		String response,
		Category category,
		String queryVector
	) {
		return RecommendRegisterDto.builder()
			.query(query)
			.response(response)
			.category(category)
			.queryVector(queryVector)
			.build();
	}
}
