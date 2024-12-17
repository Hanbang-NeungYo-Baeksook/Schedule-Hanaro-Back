package com.hanaro.schedule_hanaro.customer.service;

import java.security.Principal;

import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.response.CustomerInfoResponse;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.utils.PrincipalUtils;

@Service
public class CustomerService {
	private final CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}
	
	public CustomerInfoResponse findCustomer(Principal principal) {
		String authId = PrincipalUtils.getAuthId(principal);
		Customer customer = customerRepository.findByAuthId(authId).orElseThrow();
		return CustomerInfoResponse.of(customer.getName(), customer.getPhoneNum());
	}
}
