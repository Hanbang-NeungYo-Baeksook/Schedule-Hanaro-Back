package com.hanaro.schedule_hanaro.customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hanaro.schedule_hanaro.customer.dto.response.CustomerResponse;
import com.hanaro.schedule_hanaro.customer.service.CustomerService;
import com.hanaro.schedule_hanaro.global.dto.ResponseDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {
	private final CustomerService customerService;

	@GetMapping("/{id}")
	public ResponseDto<CustomerResponse> getCustomer(@PathVariable Long id){
		return ResponseDto.ok(customerService.getCustomerById(id));
	}
}
