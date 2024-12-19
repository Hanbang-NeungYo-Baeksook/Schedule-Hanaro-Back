package com.hanaro.schedule_hanaro.global.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
	PENDING("대기중"),
	PROGRESS("진행중"),
	COMPLETE("완료"),
	CANCELLED("취소");
	private final String status;
}
