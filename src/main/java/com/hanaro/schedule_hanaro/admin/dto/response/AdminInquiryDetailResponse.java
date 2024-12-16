package com.hanaro.schedule_hanaro.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AdminInquiryDetailResponse(
	@JsonProperty("inquiry_id") Long inquiryId,
	@JsonProperty("inquiry_content") String inquiryContent,
	@JsonProperty("category") String category,
	@JsonProperty("tags") List<String> tags,
	@JsonProperty("inquiry_created_at") LocalDateTime inquiryCreatedAt,
	@JsonProperty("reply_created_at") LocalDateTime replyCreatedAt,
	@JsonProperty("customer_name") String customerName,
	@JsonProperty("phone_number") String phoneNumber,
	@JsonProperty("reply_content") String replyContent
) {
	public static AdminInquiryDetailResponse of(
		Long inquiryId,
		String inquiryContent,
		String category,
		String tags,
		LocalDateTime inquiryCreatedAt,
		LocalDateTime replyCreatedAt,
		String customerName,
		String phoneNumber,
		String replyContent
	) {
		return AdminInquiryDetailResponse.builder()
			.inquiryId(inquiryId)
			.inquiryContent(inquiryContent)
			.category(category)
			.tags(tags != null ? List.of(tags.split(",")) : List.of())
			.inquiryCreatedAt(inquiryCreatedAt)
			.replyCreatedAt(replyCreatedAt)
			.customerName(customerName)
			.phoneNumber(phoneNumber)
			.replyContent(replyContent)
			.build();
	}
}
