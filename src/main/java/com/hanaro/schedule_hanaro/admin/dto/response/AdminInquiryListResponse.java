package com.hanaro.schedule_hanaro.admin.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;

import lombok.Builder;

@Builder
public record AdminInquiryListResponse(
	@JsonProperty("data")
	List<InquiryData> inquiryList,

	@JsonProperty("current_page")
	Integer currentPage,

	@JsonProperty("page_size")
	Integer pageSize,

	@JsonProperty("total_items")
	Long totalItems,

	@JsonProperty("total_pages")
	Integer totalPages
) {
	public static AdminInquiryListResponse from(List<InquiryData> data, Integer currentPage, Integer pageSize, Long totalItems, Integer totalPages) {
		return AdminInquiryListResponse.builder()
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

		@JsonProperty("inquiry_num")
		Integer inquiryNum,

		@JsonProperty("status")
		InquiryStatus status,

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
		public static InquiryData from(Inquiry inquiry, String customerName) {
			return InquiryData.builder()
				.inquiryId(inquiry.getId())
				.inquiryNum(inquiry.getInquiryNum())
				.status(inquiry.getInquiryStatus())
				.category(inquiry.getCategory().toString())
				.content(inquiry.getContent())
				.tags(inquiry.getTags() != null ? List.of(inquiry.getTags().split(",")) : List.of())
				.createdAt(inquiry.getCreatedAt().toString())
				.customerName(customerName)
				.build();
		}
	}
}
