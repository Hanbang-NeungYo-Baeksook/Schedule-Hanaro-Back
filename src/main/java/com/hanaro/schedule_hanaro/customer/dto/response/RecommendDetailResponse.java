package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record RecommendDetailResponse(
	@JsonProperty("recommend_id")
	Long recommendId,

	@JsonProperty("query")
	String query,

	@JsonProperty("response")
	String response,

	@JsonProperty("similarity")
	int similarity
) {
	public static RecommendDetailResponse of(
		Long recommendId, String query, String response, int similarity
	) {
		return RecommendDetailResponse.builder()
			.recommendId(recommendId)
			.query(query)
			.response(response)
			.similarity(similarity)
			.build();
	}
}
