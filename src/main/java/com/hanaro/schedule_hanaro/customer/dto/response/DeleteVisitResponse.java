package com.hanaro.schedule_hanaro.customer.dto.response;

import lombok.Builder;

@Builder
public record DeleteVisitResponse(
	Long branchId
) {
	public static DeleteVisitResponse of(
		Long branchId
	) {
		return DeleteVisitResponse.builder()
			.branchId(branchId)
			.build();
	}
}
