package com.hanaro.schedule_hanaro.global.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BranchType {
	CS("CS"),
	ATM("ATM"),
	BANK("방문점");
	private final String branchType;
}
