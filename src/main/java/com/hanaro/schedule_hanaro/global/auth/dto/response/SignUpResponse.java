package com.hanaro.schedule_hanaro.global.auth.dto.response;

import lombok.Builder;

@Builder
public record SignUpResponse(
	String message
) {
	public static SignUpResponse of(
	) {
		return SignUpResponse.builder()
			.message("회원가입에 성공하였습니다.")
			.build();
	}
}
