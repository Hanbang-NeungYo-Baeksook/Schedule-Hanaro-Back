package com.hanaro.schedule_hanaro.customer.dto.response;

import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Builder
public record InquiryListResponse(
	List<InquiryData> data,
	Pagination pagination
) {

	@Builder
	public record InquiryData(
		@JsonProperty("inquiry_id") Long inquiryId,
		@JsonProperty("inquiry_num") int inquiryNum,
		String category,
		String status,
		String content,
		List<String> tags
	) {
	}

	@Builder
	public record Pagination (
		int currentPage,
		int pageSize,
		boolean hasNext
	) {
	}
}
