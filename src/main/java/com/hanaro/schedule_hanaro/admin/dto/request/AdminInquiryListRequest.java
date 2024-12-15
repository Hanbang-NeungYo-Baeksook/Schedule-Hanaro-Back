package com.hanaro.schedule_hanaro.admin.dto.request;

import java.util.Optional;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;

import lombok.Builder;

@Builder
public record AdminInquiryListRequest(
	String status,
	Category category,
	String searchContent,
	Integer page,
	Integer size
) {
	public static AdminInquiryListRequest from(String status, Category category, String searchContent, Integer page, Integer size) {
		return AdminInquiryListRequest.builder()
			.status(Optional.ofNullable(status).orElse("pending"))
			.category(category)
			.searchContent(searchContent)
			.page(Optional.ofNullable(page).orElse(1))
			.size(Optional.ofNullable(size).orElse(5))
			.build();
	}
}
