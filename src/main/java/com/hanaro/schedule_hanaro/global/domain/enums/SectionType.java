package com.hanaro.schedule_hanaro.global.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SectionType {
	TEMP1("TEMP1"),
	TEMP2("TEMP2"),
	TEMP3("TEMP3"),
	;
	private final String type;
}
