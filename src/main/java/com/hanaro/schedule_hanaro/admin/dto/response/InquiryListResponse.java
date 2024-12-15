package com.hanaro.schedule_hanaro.admin.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;

import lombok.Builder;

@Builder
public record InquiryListResponse(
	@JsonProperty("inquiry_list")
	List<InquiryData> inquiryList, // 문의 목록 데이터

	@JsonProperty("current_page")
	Integer currentPage,           // 현재 페이지 번호

	@JsonProperty("page_size")
	Integer pageSize,              // 페이지당 데이터 개수

	@JsonProperty("total_items")
	Long totalItems,               // 전체 아이템 개수

	@JsonProperty("total_pages")
	Integer totalPages             // 전체 페이지 수
) {
	// 정적 팩토리 메서드 for DTO 변환
	public static InquiryListResponse from(List<InquiryData> data, Integer currentPage, Integer pageSize, Long totalItems, Integer totalPages) {
		return InquiryListResponse.builder()
			.inquiryList(data)
			.currentPage(currentPage)
			.pageSize(pageSize)
			.totalItems(totalItems)
			.totalPages(totalPages)
			.build();
	}

	@Builder
	public record InquiryData(
		@JsonProperty("inquiry_id")
		Long inquiryId,

		@JsonProperty("status")
		String status,

		@JsonProperty("category")
		String category,

		@JsonProperty("content")
		String content,

		@JsonProperty("tags")
		List<String> tags,

		@JsonProperty("created_at")
		String createdAt,

		@JsonProperty("customer_name")
		String customerName
	) {
		// Inquiry 엔티티에서 InquiryData DTO로 변환하는 메서드
		public static InquiryData from(Inquiry inquiry, String customerName) {
			return InquiryData.builder()
				.inquiryId(inquiry.getId())
				.status(inquiry.getInquiryStatus().toString())
				.category(inquiry.getCategory().toString())
				.content(inquiry.getContent())
				.tags(inquiry.getTags() != null ? List.of(inquiry.getTags().split(",")) : List.of())
				.createdAt(inquiry.getCreatedAt().toString())
				.customerName(customerName) // 고객 이름 전달
				.build();
		}
	}
}
