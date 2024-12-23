package com.hanaro.schedule_hanaro.admin.dto.request;

import lombok.Builder;

@Builder
public record AdminInquiryResponseRequest(
	String content  // 답변 내용
) {
	public static AdminInquiryResponseRequest of(String content) {
		return AdminInquiryResponseRequest.builder()
			.content(content)
			.build();
	}
}
