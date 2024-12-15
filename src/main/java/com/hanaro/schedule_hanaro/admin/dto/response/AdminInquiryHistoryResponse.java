package com.hanaro.schedule_hanaro.admin.dto.response;


import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;

public record AdminInquiryHistoryResponse(
	Long id,
	String content,
	Category category
) {
	public static AdminInquiryHistoryResponse from(Inquiry inquiry) {
		return new AdminInquiryHistoryResponse(
			inquiry.getId(),
			inquiry.getContent(),
			inquiry.getCategory()
		);
	}
}
