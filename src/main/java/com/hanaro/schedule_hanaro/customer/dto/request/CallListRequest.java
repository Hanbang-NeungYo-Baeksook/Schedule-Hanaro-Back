package com.hanaro.schedule_hanaro.customer.dto.request;

import lombok.Builder;

@Builder
public record CallListRequest(
	String status,
	int page,
	int size
) {}
