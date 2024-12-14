package com.hanaro.schedule_hanaro.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record InquiryResponse(
	@JsonProperty("inquiry_id")
	Long inquiryId,

	@JsonProperty("inquiry_num")
	Integer inquiryNum,

	String category,
	String status,
	String content,

	@JsonProperty("tags")
	List<String> tags
) {
	public static InquiryResponse of(
		Long inquiryId,
		Integer inquiryNum,
		String category,
		String status,
		String content,
		List<String> tags
	) {
		return InquiryResponse.builder()
			.inquiryId(inquiryId)
			.inquiryNum(inquiryNum)
			.category(category)
			.status(status)
			.content(content)
			.tags(tags)
			.build();
	}
}
