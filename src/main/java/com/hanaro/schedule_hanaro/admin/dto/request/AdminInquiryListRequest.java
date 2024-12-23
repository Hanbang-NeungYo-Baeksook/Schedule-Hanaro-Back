package com.hanaro.schedule_hanaro.admin.dto.request;

import java.util.Optional;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import lombok.Builder;

@Builder
public record AdminInquiryListRequest(
	InquiryStatus inquiryStatus,
	Category category,
	String searchContent,
	Integer page,
	Integer size
) {
	public static AdminInquiryListRequest from(InquiryStatus inquiryStatus, Category category, String searchContent, Integer page, Integer size) {
		return AdminInquiryListRequest.builder()
			.inquiryStatus(Optional.ofNullable(inquiryStatus).orElse(InquiryStatus.PENDING))
			.category(Optional.ofNullable(category).orElse(Category.SIGNIN))
			.searchContent(searchContent)
			.page(Optional.ofNullable(page).orElse(1))
			.size(Optional.ofNullable(size).orElse(5))
			.build();
	}
}
