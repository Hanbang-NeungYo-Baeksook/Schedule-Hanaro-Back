package com.hanaro.schedule_hanaro.customer.dto.response;

import com.hanaro.schedule_hanaro.global.domain.Branch;

public record BranchWithMetrics(
	Branch branch,       // 영업점 정보
	double distance,     // 사용자와 영업점 간 거리
	int waitAmount       // 총 대기 인원
) {
	public static BranchWithMetrics of(Branch branch, double distance, int waitAmount) {
		return new BranchWithMetrics(branch, distance, waitAmount);
	}
}
