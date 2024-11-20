package com.hanaro.schedule_hanaro.customer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hanaro.schedule_hanaro.customer.dto.request.CustomerCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.CustomerResponse;
import com.hanaro.schedule_hanaro.customer.service.CustomerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer")
public class CustomerController {
	private final CustomerService customerService;

	@GetMapping("/{id}")
	public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id){
		return ResponseEntity.ok().body(customerService.getCustomerById(id));
	}

	@PostMapping("/create")
	public ResponseEntity<String>createCustomer(@RequestBody CustomerCreateRequest customerCreateRequest){
		return ResponseEntity.ok().body(customerService.createCustomer(customerCreateRequest));
	}

}
