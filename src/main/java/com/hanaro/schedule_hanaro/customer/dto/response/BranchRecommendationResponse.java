package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record BranchRecommendationResponse(
	@JsonProperty("branch_id") Long id,         // 영업점 ID
	@JsonProperty("branch_name") String branchName, // 영업점 이름
	@JsonProperty("address") String address,    // 영업점 주소
	@JsonProperty("distance") String distance,  // 사용자와 영업점 거리
	@JsonProperty("wait_time") String waitTime, // 예상 대기 시간
	@JsonProperty("branch_info") BankInfoDto branchInfo
) {
	public static BranchRecommendationResponse of(Long id, String branchName, String address, double distance, int waitTime, BankInfoDto branchInfo) {
		return BranchRecommendationResponse.builder()
			.id(id)
			.branchName(branchName)
			.address(address)
			.distance(String.format("%.0f m", distance * 1000)) // 거리 : m 표기
			.waitTime(String.format("%d분", waitTime))
			.branchInfo(branchInfo)// 대기 시간 : 분
			.build();
	}
}
