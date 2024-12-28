package com.hanaro.schedule_hanaro.global.auth.dto.response;

import lombok.Builder;

@Builder
public record SignInResponse(
	String accessToken,
	String refreshToken
) {
	public static SignInResponse of(
		String accessToken,
		String refreshToken
	) {
		return SignInResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

}
