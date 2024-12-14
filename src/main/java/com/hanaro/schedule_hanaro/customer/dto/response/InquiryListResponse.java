package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record InquiryListResponse(
	@JsonProperty("inquiry_list")
	List<InquiryResponse> inquiryList,

	@JsonProperty("current_page")
	Integer currentPage,

	@JsonProperty("page_size")
	Integer pageSize,

	@JsonProperty("total_items")
	Integer totalItems,

	@JsonProperty("total_pages")
	Integer totalPages
) {
	public static InquiryListResponse of(
		List<InquiryResponse> inquiryList,
		Integer currentPage,
		Integer pageSize,
		Integer totalItems,
		Integer totalPages
) {
		return InquiryListResponse.builder()
			.inquiryList(inquiryList)
			.currentPage(currentPage)
			.pageSize(pageSize)
			.totalItems(totalItems)
			.totalPages(totalPages)
			.build();
	}
}
