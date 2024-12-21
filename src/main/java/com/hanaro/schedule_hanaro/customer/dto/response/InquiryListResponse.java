package com.hanaro.schedule_hanaro.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
public record InquiryListResponse(
	List<InquiryResponse> inquiryList,
	Pagination pagination
) {
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Pagination {
		int currentPage;
		int pageSize;
		int totalItems;
		int totalPages;
		boolean hasNext;
	}
}
