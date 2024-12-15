package com.hanaro.schedule_hanaro.admin.dto.request;

import java.util.Optional;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;

import lombok.Builder;

@Builder
public record InquiryListRequest(
	String status,
	Category category,
	String searchContent,
	Integer page,
	Integer size
) {
	public static InquiryListRequest from(String status, Category category, String searchContent, Integer page, Integer size) {
		return InquiryListRequest.builder()
			.status(Optional.ofNullable(status).orElse("pending"))
			.category(category)
			.searchContent(searchContent)
			.page(Optional.ofNullable(page).orElse(1))
			.size(Optional.ofNullable(size).orElse(5))
			.build();
	}
}
