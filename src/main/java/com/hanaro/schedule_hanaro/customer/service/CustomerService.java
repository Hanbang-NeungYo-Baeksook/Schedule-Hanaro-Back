package com.hanaro.schedule_hanaro.customer.service;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.response.CustomerInfoResponse;
import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
import com.hanaro.schedule_hanaro.global.repository.CallRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.global.utils.PrincipalUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
	private final CustomerRepository customerRepository;
	private final CallRepository callRepository;
	private final InquiryRepository inquiryRepository;
	
	public CustomerInfoResponse findCustomer(Authentication authentication) {
		Long id = PrincipalUtils.getId(authentication);
		Customer customer = customerRepository.findById(id).orElseThrow();
		Integer callAmount=callRepository.countCallsByCustomer(customer);
		Integer inquiryAmount = inquiryRepository.countInquiryByCustomer(customer);
		return CustomerInfoResponse.of(customer.getName(), customer.getAuthId(), customer.getBirth().toString(),
			customer.getPhoneNum(), callAmount, inquiryAmount);
	}
}
