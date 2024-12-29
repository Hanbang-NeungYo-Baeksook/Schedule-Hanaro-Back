package com.hanaro.schedule_hanaro.global.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SectionType {
	DEPOSIT("예금",1),
	PERSONAL_LOAN("개인대출",2),
	OTHERS("기타",3);
	private final String type;
	private final int order;
}
