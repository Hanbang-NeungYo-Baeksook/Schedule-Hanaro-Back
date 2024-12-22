package com.hanaro.schedule_hanaro.global.auth.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public record AuthSignUpRequest(

	String authId,
	String password,
	String name,
	String phoneNum,
	LocalDate birth,
	String gender
) {

}
