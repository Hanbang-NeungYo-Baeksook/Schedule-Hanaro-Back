package com.hanaro.schedule_hanaro.customer.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record BranchListResponse(
	@JsonProperty("bank_list")
	List<BankInfoDto> bankList,
	@JsonProperty("atm_list")
	List<AtmInfoDto> atmList
) {
	public static BranchListResponse of(
		@JsonProperty("bank_list") final List<BankInfoDto> bankList,
		@JsonProperty("atm_list") final List<AtmInfoDto> atmList
	) {
		return BranchListResponse.builder()
			.bankList(bankList)
			.atmList(atmList)
			.build();
	}
}
