package com.hanaro.schedule_hanaro.customer.dto.request;
import lombok.Builder;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

@Builder
public record CallRequest(

	@JsonProperty("call_date")
	LocalDateTime callDate,

	String category,
	String content
) {
}
