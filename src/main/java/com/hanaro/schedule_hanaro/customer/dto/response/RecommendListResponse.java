package com.hanaro.schedule_hanaro.customer.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record RecommendListResponse(
	@JsonProperty("recommends")
	List<RecommendDetailResponse> recommends,

	@JsonProperty("tags")
	String tags
) {
	public static RecommendListResponse of(
		List<RecommendDetailResponse> recommends,
		String tags
	) {
		return RecommendListResponse.builder()
			.recommends(recommends)
			.tags(tags)
			.build();
	}
}
