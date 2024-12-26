package com.hanaro.schedule_hanaro.admin.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.CallMemo;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;

public record AdminCallInfoResponse(
	Long id,
	@JsonProperty("waiting_num")
	int waitingNum,
	String category,
	String tags,
	String content,
	@JsonProperty("reservation_time")
	LocalDateTime reservationTime,
	@JsonProperty("start_time")
	LocalDateTime startTime,
	@JsonProperty("end_time")
	LocalDateTime endTime,
	@JsonProperty("customer_id")
	Long customerId,
	String memo
) {
	public static AdminCallInfoResponse from(final Call call,
		final CallMemo memo) {
		return new AdminCallInfoResponse(
			call.getId(),
			call.getCallNum(),
			call.getCategory().toString(),
			call.getTags(),
			call.getContent(),
			call.getCallDate(),
			call.getStartedAt(),
			call.getEndedAt(),
			call.getCustomer().getId(),
			memo == null ? null : memo.getContent()
		);
	}
}
