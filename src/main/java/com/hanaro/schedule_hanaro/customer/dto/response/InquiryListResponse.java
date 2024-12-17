package com.hanaro.schedule_hanaro.customer.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record InquiryListResponse(
	List<InquiryResponse> inquiryList,
	Pagination pagination
) {
	@Builder
	public static class Pagination {
		int currentPage;
		int pageSize;
		int totalItems;
		int totalPages;
		boolean hasNext;
	}
}
