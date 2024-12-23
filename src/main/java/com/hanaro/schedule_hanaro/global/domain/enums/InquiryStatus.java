package com.hanaro.schedule_hanaro.global.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquiryStatus {
	PENDING("답변 대기중"),
	REGISTRATIONCOMPLETE("답변완료");
	private final String inquiryStatus;

	// public static InquiryStatus from(String value) {
	// 	for (InquiryStatus status : InquiryStatus.values()) {
	// 		if (status.name().equalsIgnoreCase(value) || status.inquiryStatus.equalsIgnoreCase(value)) {
	// 			return status;
	// 		}
	// 	}
	// 	throw new IllegalArgumentException("Invalid InquiryStatus: " + value);
	// }



	@Override
	public String toString() {
		return this.inquiryStatus;
	}
}
