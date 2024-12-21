package com.hanaro.schedule_hanaro.customer.vo;

import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;

public record BankVO(
	Long branchId,
	String name,
	String xPosition,
	String yPosition,
	String address,
	BranchType branchType,
	Integer waitTime,
	Integer waitAmount
) {

}
