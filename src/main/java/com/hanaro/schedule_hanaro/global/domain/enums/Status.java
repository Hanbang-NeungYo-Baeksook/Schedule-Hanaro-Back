package com.hanaro.schedule_hanaro.global.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
	PENDING("대기중"),
	PROGRESS("진행중"),
	REGISTRATIONCOMPLETE("답변완료"),
	ANSWERCOMPLETE("등록완료");
	private final String status;
}
