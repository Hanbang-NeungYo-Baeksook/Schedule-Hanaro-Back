package com.hanaro.schedule_hanaro.customer.dto;

import lombok.Builder;

@Builder
public record RegisterReservationDto(
	Long csVisitId,
	Long sectionId,
	Integer waitTime
) {
	public static RegisterReservationDto of(
		Long csVisitId,
		Long sectionId,
		Integer waitTime
	) {
		return RegisterReservationDto.builder()
			.csVisitId(csVisitId)
			.sectionId(sectionId)
			.waitTime(waitTime)
			.build();
	}
}
