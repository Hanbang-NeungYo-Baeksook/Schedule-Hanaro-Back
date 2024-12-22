package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotAvailabilityResponse {
	@JsonProperty("time_slot")
	private String timeSlot;

	@JsonProperty("available_slots")
	private int availableSlots;
}
