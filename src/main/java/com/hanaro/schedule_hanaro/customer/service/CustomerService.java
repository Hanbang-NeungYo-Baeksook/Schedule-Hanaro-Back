package com.hanaro.schedule_hanaro.customer.service;


import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.response.CustomerInfoResponse;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
	private final CustomerRepository customerRepository;

	public CustomerInfoResponse findCustomerById(Long id) {
		Customer customer= customerRepository.findById(id).orElseThrow();
		return CustomerInfoResponse.of(customer.getName(),customer.getPhoneNum());
	}
}