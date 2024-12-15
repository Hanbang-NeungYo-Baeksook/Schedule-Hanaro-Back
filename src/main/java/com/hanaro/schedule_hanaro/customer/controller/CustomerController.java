package com.hanaro.schedule_hanaro.customer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.customer.dto.response.CustomerInfoResponse;
import com.hanaro.schedule_hanaro.customer.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
	private final CustomerService customerService;

	@GetMapping("/{customer-id}")
	public ResponseEntity<CustomerInfoResponse> getCustomerInfo(@PathVariable("customer-id") Long customerId) {
		System.out.println("get customer controller 진입" + customerId);
		ResponseEntity<CustomerInfoResponse> response = ResponseEntity.ok()
			.body(customerService.findCustomerById(customerId));
		System.out.println("response 완료" + response);
		return response;
	}
}
