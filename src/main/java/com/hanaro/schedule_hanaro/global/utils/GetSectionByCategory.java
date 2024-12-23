package com.hanaro.schedule_hanaro.global.utils;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;

public class GetSectionByCategory {
	public static SectionType getSectionTypeByCategory(Category category) {
		switch (category) {
			case DEPOSIT, TRUST, FUND, FOREX, CD -> {
				return SectionType.DEPOSIT;
			}
			case LOAN -> {
				return SectionType.PERSONAL_LOAN;
			}
			default -> {
				return SectionType.OTHERS;
			}
		}
	}
}
