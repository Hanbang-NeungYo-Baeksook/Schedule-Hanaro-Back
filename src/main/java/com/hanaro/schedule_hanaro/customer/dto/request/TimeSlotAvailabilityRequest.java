package com.hanaro.schedule_hanaro.customer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotAvailabilityRequest {
	@JsonProperty("date")
	private LocalDate date;
}
