package com.hanaro.schedule_hanaro.customer.vo;

import lombok.Builder;

@Builder
public record ReservedListVO(
	Long visitId,
	Long branchId
) {
	public static ReservedListVO of(
		Long visitId,
		Long branchId
	) {
		return ReservedListVO.builder()
			.visitId(visitId)
			.branchId(branchId)
			.build();
	}
}
