package com.hanaro.schedule_hanaro.admin.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record AdminInquiryResponse(
	@JsonProperty("inquiry_id") Long inquiryId,
	@JsonProperty("admin_id") Long adminId,
	@JsonProperty("content") String content,
	@JsonProperty("created_at") LocalDateTime createdAt
) {
	public static AdminInquiryResponse of(Long inquiryId, Long adminId, String content, LocalDateTime createdAt) {
		return AdminInquiryResponse.builder()
			.inquiryId(inquiryId)
			.adminId(adminId)
			.content(content)
			.createdAt(createdAt)
			.build();
	}
}
