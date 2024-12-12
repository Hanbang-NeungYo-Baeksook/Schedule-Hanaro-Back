package com.hanaro.schedule_hanaro.customer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hanaro.schedule_hanaro.customer.dto.response.CustomerResponse;
import com.hanaro.schedule_hanaro.customer.service.CustomerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {
	private final CustomerService customerService;

	@GetMapping("/{customer_id}")
	public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long customer_id){
		System.out.println("get customer controller 진입"+customer_id);
		ResponseEntity<CustomerResponse> response = ResponseEntity.ok().body(customerService.getCustomerById(customer_id));
		System.out.println("response 완료"+response);
		return response;
	}
}
