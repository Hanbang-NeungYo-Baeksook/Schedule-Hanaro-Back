package com.hanaro.schedule_hanaro.global.auth.dto.request;

public record AuthAdminSignUpRequest(
	String authId,
	String password,
	String name
) {
}
