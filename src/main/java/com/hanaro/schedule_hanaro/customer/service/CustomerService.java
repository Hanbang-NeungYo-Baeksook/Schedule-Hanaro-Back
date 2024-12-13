package com.hanaro.schedule_hanaro.customer.service;


import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.response.CustomerResponse;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
	private final CustomerRepository customerRepository;

	public CustomerResponse findCustomerById(Long id) {
		Customer customer= customerRepository.findById(id).orElseThrow();
		return CustomerResponse.from(customer.getName(),customer.getPhoneNum());
	}
}