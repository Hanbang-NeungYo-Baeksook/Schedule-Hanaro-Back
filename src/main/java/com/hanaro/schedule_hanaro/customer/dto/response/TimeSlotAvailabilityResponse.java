package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
public record TimeSlotAvailabilityResponse(
	@JsonProperty("time_slot") String timeSlot,
	@JsonProperty("available_slots") int availableSlots
) {
	public static TimeSlotAvailabilityResponse of(String timeSlot, int availableSlots) {
		return TimeSlotAvailabilityResponse.builder()
			.timeSlot(timeSlot)
			.availableSlots(availableSlots)
			.build();
	}
}
