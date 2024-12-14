package com.hanaro.schedule_hanaro.customer.dto.request;

import lombok.Builder;

@Builder
public record InquiryListRequest(
	String status,  // 상담 상태: "pending" 또는 "completed"
	Integer page, // 페이지 번호(디폴트: 1)
	Integer size
) {
	public static InquiryListRequest of(String status, Integer page, Integer size) {
		return InquiryListRequest.builder()
			.status(status)
			.page(page != null ? page : 1)
			.size(size != null ? size : 5) // 카드 개수(디폴트 : 5)
			.build();
	}
}
