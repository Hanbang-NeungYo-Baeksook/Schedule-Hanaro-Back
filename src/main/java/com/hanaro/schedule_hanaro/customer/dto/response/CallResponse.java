package com.hanaro.schedule_hanaro.customer.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record CallResponse(
	@JsonProperty("call_id")
	Long callId
) {
}
