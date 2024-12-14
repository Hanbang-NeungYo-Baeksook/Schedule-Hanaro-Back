package com.hanaro.schedule_hanaro.global.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquiryStatus {
	PENDING("답변 대기중"),
	REGISTRATIONCOMPLETE("답변완료");
	private final String inquiryStatus;
}
