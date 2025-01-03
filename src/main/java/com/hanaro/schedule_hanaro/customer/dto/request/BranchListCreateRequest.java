package com.hanaro.schedule_hanaro.customer.dto.request;

import java.util.List;

public record BranchListCreateRequest(
	List<BranchDto> branches
) {
	public record BranchDto(
		String name,
		String type,
		String position_x,
		String position_y,
		String address,
		String tel,
		String business_hours
	){

	}
}
