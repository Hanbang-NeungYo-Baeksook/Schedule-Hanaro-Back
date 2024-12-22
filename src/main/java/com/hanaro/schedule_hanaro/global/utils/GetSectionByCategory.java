package com.hanaro.schedule_hanaro.global.utils;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;

public class GetSectionByCategory {
	public static SectionType getSectionTypeByCategory(Category category) {
		switch (category) {
			case FUND, DEPOSIT -> {
				return SectionType.DEPOSIT;
			}
			case FOREX, LOAN -> {
				return SectionType.PERSONAL_LOAN;
			}
			default -> {
				return SectionType.BUSINESS_LOAN;
			}
		}
	}
}
