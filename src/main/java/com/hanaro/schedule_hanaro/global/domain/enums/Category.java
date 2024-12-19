package com.hanaro.schedule_hanaro.global.domain.enums;

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
}
