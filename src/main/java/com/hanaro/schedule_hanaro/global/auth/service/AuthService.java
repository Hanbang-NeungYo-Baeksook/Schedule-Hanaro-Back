package com.hanaro.schedule_hanaro.global.auth.service;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.global.auth.dto.request.AuthSignUpRequest;
import com.hanaro.schedule_hanaro.customer.domain.Customer;
import com.hanaro.schedule_hanaro.customer.domain.enums.Gender;
import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final CustomerRepository customerRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	public void signUp(AuthSignUpRequest authSignUpRequest){
		customerRepository.save(Customer.builder()
			.authId(authSignUpRequest.authId())
			.password(bCryptPasswordEncoder.encode(authSignUpRequest.password()))
			.name(authSignUpRequest.name())
			.phoneNum(authSignUpRequest.phoneNum())
			.birth(authSignUpRequest.birth())
			.gender(Gender.valueOf(authSignUpRequest.gender()))
			.build());
	}
}
