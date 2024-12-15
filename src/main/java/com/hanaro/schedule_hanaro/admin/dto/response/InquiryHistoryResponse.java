package com.hanaro.schedule_hanaro.admin.dto.response;


import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;

public record InquiryHistoryResponse(
	Long id,
	String content,
	Category category
) {
	public static InquiryHistoryResponse from(Inquiry inquiry) {
		return new InquiryHistoryResponse(
			inquiry.getId(),
			inquiry.getContent(),
			inquiry.getCategory()
		);
	}
}
