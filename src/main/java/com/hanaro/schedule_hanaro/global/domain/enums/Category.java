package com.hanaro.schedule_hanaro.global.domain.enums;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
	SIGNIN("로그인",1),
	SIGNUP("회원가입",1),
	AUTH("인증",3),
	DEPOSIT("예금",5),
	TRUST("신탁",6),
	FUND("펀드",7),
	LOAN("대출",30),
	FOREX("외환",20),
	INERNET_BANKING("인터넷뱅킹",13),
	HANAONEQ("하나원큐",7),
	PHONE_BANKING("폰뱅킹",6),
	CD("CD",14),
	ATM("ATM",45),
	UTILITY_BILL("공과금납부",23),
	FOREIGN("해외",25),
	BRANCH("영업점",20);
	private final String category;
	private final int waitTime;

	// public static Category from(String value) {
	// 	for (Category category : Category.values()) {
	// 		if (category.name().equalsIgnoreCase(value)) { // 대소문자 무시 매핑
	// 			return category;
	// 		}
	// 	}
	// 	throw new IllegalArgumentException("Invalid Category: " + value);
	// }

	@Override
	public String toString() {
		return this.category;
	}

	public static Category fromCategoryName(String categoryName) {
		return Arrays.stream(values())
			.filter(category -> category.getCategory().equals(categoryName))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리입니다: " + categoryName));
	}
}
