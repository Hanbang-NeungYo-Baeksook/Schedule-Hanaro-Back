package com.hanaro.schedule_hanaro.admin.dto.request;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;

import lombok.Builder;

@Builder
public record InquiryListRequest(
	String status,          // 상태 필터링 (pending, completed)
	Category category,      // 문의 카테고리 (ENUM 적용)
	String searchContent,   // 검색어 (고객명, 태그, 문의 내용 포함)
	Integer page,           // 페이지 번호
	Integer size            // 페이지당 데이터 개수
) {
	// 기본값 설정을 위한 Builder 메서드
	public static InquiryListRequest of(String status, Category category, String searchContent, Integer page, Integer size) {
		return InquiryListRequest.builder()
			.status(status != null ? status : "pending")
			.category(category) // Category ENUM 그대로 사용
			.searchContent(searchContent)
			.page(page != null ? page : 1)
			.size(size != null ? size : 5)
			.build();
	}
}
