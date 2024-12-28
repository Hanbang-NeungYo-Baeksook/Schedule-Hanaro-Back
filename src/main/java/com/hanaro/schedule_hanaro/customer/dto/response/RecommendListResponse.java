package com.hanaro.schedule_hanaro.customer.dto.response;

import java.util.List;

import org.apache.commons.math3.linear.RealVector;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record RecommendListResponse(
	RealVector queryVector,

	@JsonProperty("recommends")
	List<RecommendDetailResponse> recommends,

	@JsonProperty("tags")
	List<String> tags
) {
	public static RecommendListResponse of(
		RealVector queryVector,
		List<RecommendDetailResponse> recommends,
		List<String> tags
	) {
		return RecommendListResponse.builder()
			.queryVector(queryVector)
			.recommends(recommends)
			.tags(tags)
			.build();
	}
}
