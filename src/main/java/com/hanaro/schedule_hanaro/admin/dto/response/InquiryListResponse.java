package com.hanaro.schedule_hanaro.admin.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;

import lombok.Builder;

@Builder
public record InquiryListResponse(
	@JsonProperty("inquiry_list")
	List<Inquiry> inquiryList,

	@JsonProperty("current_page")
	Integer currentPage,

	@JsonProperty("page_size")
	Integer pageSize,

	@JsonProperty("total_items")
	Integer totalItems,

	@JsonProperty("total_pages")
	Integer totalPages
) {
	public static com.hanaro.schedule_hanaro.customer.dto.response.InquiryListResponse of(
		List<InquiryResponse> inquiryList,
		Integer currentPage,
		Integer pageSize,
		Integer totalItems,
		Integer totalPages
	) {
		return com.hanaro.schedule_hanaro.customer.dto.response.InquiryListResponse.builder()
			.inquiryList(inquiryList)
			.currentPage(currentPage)
			.pageSize(pageSize)
			.totalItems(totalItems)
			.totalPages(totalPages)
			.build();
	}
}
