package com.hanaro.schedule_hanaro.admin.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;

public record CallInfoResponse(
	Long id,
	int waitingNum,
	Category category,
	String tags,
	String content,
	LocalDateTime reservationTime,
	LocalDateTime startTime,
	LocalDateTime endTime,

	String userName,
	String authId,
	String mobile,
	LocalDate birthDt,

	List<CallHistoryResponse> calls,
	List<InquiryHistoryResponse> inquires
) {
	public static CallInfoResponse from(final Call call, final Customer customer,
		final List<CallHistoryResponse> calls,
		final List<InquiryHistoryResponse> inquires) {
		return new CallInfoResponse(
			call.getId(),
			call.getCallNum(),
			call.getCategory(),
			call.getTags(),
			call.getContent(),
			call.getCallDate(),
			call.getStartedAt(),
			call.getEndedAt(),
			customer.getName(),
			customer.getAuthId(),
			customer.getPhoneNum(),
			customer.getBirth(),
			calls,
			inquires
		);
	}
}
