package com.hanaro.schedule_hanaro.customer.dto;

import lombok.Builder;

@Builder
public record CancelReservationDto(
	Long sectionId,
	Integer waitTime) {
	public static CancelReservationDto of(
		Long sectionId,
		Integer waitTime
	) {
		return CancelReservationDto.builder()
			.sectionId(sectionId)
			.waitTime(waitTime)
			.build();
	}

}
