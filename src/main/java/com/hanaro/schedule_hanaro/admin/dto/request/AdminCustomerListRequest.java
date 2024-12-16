package com.hanaro.schedule_hanaro.admin.dto.request;

import lombok.Builder;

@Builder
public record AdminCustomerListRequest(
	Integer page,
	Integer size
) {
	public static AdminCustomerListRequest from(Integer page, Integer size) {
		return AdminCustomerListRequest.builder()
			.page(page != null ? page : 1)
			.size(size != null ? size : 10)
			.build();
	}
}
