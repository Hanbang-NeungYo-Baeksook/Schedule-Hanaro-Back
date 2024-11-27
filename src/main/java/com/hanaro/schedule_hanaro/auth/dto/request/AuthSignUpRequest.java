package com.hanaro.schedule_hanaro.auth.dto.request;

import java.time.LocalDate;

public record AuthSignUpRequest(

	String authId,
	String password,
	String name,
	String phoneNum,
	LocalDate birth,
	String gender
) {

}
