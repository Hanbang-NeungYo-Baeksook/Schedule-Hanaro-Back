package com.hanaro.schedule_hanaro.customer.service;


import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.request.CustomerCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.CustomerResponse;
import com.hanaro.schedule_hanaro.customer.entity.Customer;
import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
	private final CustomerRepository customerRepository;

	public CustomerResponse getCustomerById(long id) {
		Customer customer = customerRepository.findById(id);
		return new CustomerResponse(customer.getCustomerName(), customer.getPhoneNum());
	}

	public String createCustomer(CustomerCreateRequest customerCreateRequest){
		customerRepository.save(Customer.of(customerCreateRequest.name(),customerCreateRequest.phoneNum()));
		return "Success";
	}
}
