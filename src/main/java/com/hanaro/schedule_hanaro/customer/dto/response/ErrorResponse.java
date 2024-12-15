package com.hanaro.schedule_hanaro.customer.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorResponse(
	@JsonProperty("code")
	String code,

	@JsonProperty("error")
	String error
) {}
