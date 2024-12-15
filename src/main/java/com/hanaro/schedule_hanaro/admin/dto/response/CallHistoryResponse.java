package com.hanaro.schedule_hanaro.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;

public record CallHistoryResponse(
	Long id,
	String content,
	Category category
) {
	public static CallHistoryResponse from(final Call call) {
		return new CallHistoryResponse(
			call.getId(),
			call.getContent(),
			call.getCategory()
		);
	}
}
