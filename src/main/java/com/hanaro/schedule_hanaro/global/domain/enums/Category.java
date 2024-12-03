package com.hanaro.schedule_hanaro.global.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
	SIGNIN("로그인"),
	SIGNUP("회원가입"),
	AUTH("인증"),
	DEPOSIT("예금"),
	TRUST("신탁"),
	FUND("펀드"),
	LOAN("대출"),
	FOREX("외환"),
	INERNET_BANKING("인터넷뱅킹"),
	HANAONEQ("하나원큐"),
	PHONE_BANKING("폰뱅킹"),
	CD("CD"),
	ATM("ATM"),
	UTILITY_BILL("공과금납부"),
	FOREIGN("해외"),
	BRANCH("영업점");
	private final String category;
}
