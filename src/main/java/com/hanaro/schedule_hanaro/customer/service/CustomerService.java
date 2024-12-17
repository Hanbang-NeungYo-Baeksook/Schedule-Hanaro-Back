package com.hanaro.schedule_hanaro.customer.service;

import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.response.CustomerInfoResponse;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;

@Service
public class CustomerService {
	private final CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}
	
	public CustomerInfoResponse findCustomerById(Long id) {
		Customer customer = customerRepository.findById(id).orElseThrow();
		return CustomerInfoResponse.of(customer.getName(), customer.getPhoneNum());
	}
}
