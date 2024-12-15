package com.hanaro.schedule_hanaro.global.auth.dto.response;

import lombok.Builder;

@Builder
public record JwtTokenDto(
	String accessToken,
	String refreshToken
) {
	public static JwtTokenDto of(
		String accessToken,
		String refreshToken
	) {
		return JwtTokenDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
