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
	@JsonProperty("waiting_num")
	int waitingNum,
	Category category,
	String tags,
	String content,
	@JsonProperty("reservation_time")
	LocalDateTime reservationTime,
	@JsonProperty("start_time")
	LocalDateTime startTime,
	@JsonProperty("end_time")
	LocalDateTime endTime,
	@JsonProperty("user_name")
	String userName,
	@JsonProperty("auth_id")
	String authId,
	String mobile,
	@JsonProperty("birth_dt")
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
