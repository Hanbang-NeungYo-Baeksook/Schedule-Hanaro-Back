package com.hanaro.schedule_hanaro.customer.vo;

import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;

public record BankVO(
	Long branchId,
	String name,
	String xPosition,
	String yPosition,
	String address,
	String tel,
	String businessHours,
	BranchType branchType,
	SectionType sectionType,
	Integer waitTime,
	Integer waitAmount
) {

}
