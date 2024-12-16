package com.hanaro.schedule_hanaro.admin.dto.request;

import lombok.Builder;

@Builder
public record AdminInquiryResponseRequest(
	Long adminId,   // 관리자 ID
	String content  // 답변 내용
) {
	public static AdminInquiryResponseRequest of(Long adminId, String content) {
		return AdminInquiryResponseRequest.builder()
			.adminId(adminId)
			.content(content)
			.build();
	}
}
