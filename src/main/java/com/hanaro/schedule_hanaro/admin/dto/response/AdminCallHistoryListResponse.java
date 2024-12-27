package com.hanaro.schedule_hanaro.admin.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.customer.dto.response.CallListResponse;

import lombok.Builder;

@Builder
public record AdminCallHistoryListResponse(
	@JsonProperty("data")
	List<AdminCallHistoryResponse> calls,

	@JsonProperty("current_page")
	Integer currentPage,           // 현재 페이지 번호

	@JsonProperty("page_size")
	Integer pageSize,              // 페이지당 데이터 개수

	@JsonProperty("total_items")
	Long totalItems,               // 전체 아이템 개수

	@JsonProperty("total_pages")
	Integer totalPages             // 전체 페이지 수
) {
	public static AdminCallHistoryListResponse from(List<AdminCallHistoryResponse> calls, Integer currentPage, Integer pageSize, Long totalItems, Integer totalPages) {
		return AdminCallHistoryListResponse.builder()
			.calls(calls)
			.currentPage(currentPage)
			.pageSize(pageSize)
			.totalItems(totalItems)
			.totalPages(totalPages)
			.build();
	}
}
