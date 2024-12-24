package com.hanaro.schedule_hanaro.customer.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record BranchRecommendationResponse(
	@JsonProperty("recommend_list")
	List<BranchRecommendationData> recommendationDataList
) {
	public static BranchRecommendationResponse of(
		@JsonProperty("recommend_list") final List<BranchRecommendationData> recommendationDataList
	) {
		return BranchRecommendationResponse.builder()
			.recommendationDataList(recommendationDataList)
			.build();
	}
}
