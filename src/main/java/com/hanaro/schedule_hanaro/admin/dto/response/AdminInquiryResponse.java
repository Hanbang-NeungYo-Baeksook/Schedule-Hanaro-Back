package com.hanaro.schedule_hanaro.admin.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record AdminInquiryResponse(
	@JsonProperty("inquiry_id") Long inquiryId,
	@JsonProperty("admin_id") Long adminId,
	@JsonProperty("content") String content,
	@JsonProperty("created_at") LocalDateTime
		createdAt,
	@JsonProperty("updated_at") LocalDateTime updatedAt
) {
	public static AdminInquiryResponse of(Long inquiryId, Long adminId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
		return AdminInquiryResponse.builder()
			.inquiryId(inquiryId)
			.adminId(adminId)
			.content(content)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}
}
