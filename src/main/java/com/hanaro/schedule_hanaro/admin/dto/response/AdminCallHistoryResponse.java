package com.hanaro.schedule_hanaro.admin.dto.response;

import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;

import lombok.Builder;

@Builder
public record AdminCallHistoryResponse(
	Long id,
	String content,
	String category
) {
	public static AdminCallHistoryResponse from(final Call call) {
		return new AdminCallHistoryResponse(
			call.getId(),
			call.getContent(),
			call.getCategory().toString()
		);
	}
}
