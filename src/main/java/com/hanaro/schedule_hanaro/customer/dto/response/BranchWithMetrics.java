package com.hanaro.schedule_hanaro.customer.dto.response;

import com.hanaro.schedule_hanaro.global.domain.Branch;

public record BranchWithMetrics(
	Branch branch,       // 영업점 정보
	double distance,     // 사용자와 영업점 간 거리
	int expectedWaitNum, // 예상 대기 인원 (총 대기 인원 - 현재 대기 인원)
	int currentNum,      // 현재 대기 인원
	int waitAmount       // 총 대기 인원
) {
	public static BranchWithMetrics of(Branch branch, double distance, int expectedWaitNum, int currentNum, int waitAmount) {
		return new BranchWithMetrics(branch, distance, expectedWaitNum, currentNum, waitAmount);
	}
}
