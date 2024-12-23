package com.hanaro.schedule_hanaro.global.domain.enums;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SectionType {
	DEPOSIT("예금"),
	PERSONAL_LOAN("개인대출"),
	OTHERS("기타");
	private final String type;
}
