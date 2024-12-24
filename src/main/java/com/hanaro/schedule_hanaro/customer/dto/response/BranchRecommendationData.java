package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record BranchRecommendationData(
	@JsonProperty("branch_id") Long id,         // 영업점 ID
	@JsonProperty("branch_name") String branchName, // 영업점 이름
	@JsonProperty("address") String address,    // 영업점 주소
	@JsonProperty("distance") Integer distance,  // 사용자와 영업점 거리
	@JsonProperty("wait_time") Integer waitTime, // 예상 대기 시간
	@JsonProperty("current_num") Integer currentNum // 현재 대기 인원
	// @JsonProperty("branch_info") BankInfoDto branchInfo
) {
	public static BranchRecommendationData of(Long id, String branchName, String address, double distance, int waitTime,
		int currentNum) {
		return BranchRecommendationData.builder()
			.id(id)
			.branchName(branchName)
			.address(address)
			.distance((int)(distance * 1000)) // 거리 : m 표기
			.waitTime(waitTime)
			.currentNum(currentNum)
			// .branchInfo(branchInfo)// 대기 시간 : 분
			.build();
	}
}
