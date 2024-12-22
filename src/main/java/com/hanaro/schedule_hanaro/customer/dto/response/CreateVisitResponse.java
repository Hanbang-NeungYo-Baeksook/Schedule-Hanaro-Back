package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateVisitResponse(
	@JsonProperty("visit_id")
	Long visitId
) {
}
