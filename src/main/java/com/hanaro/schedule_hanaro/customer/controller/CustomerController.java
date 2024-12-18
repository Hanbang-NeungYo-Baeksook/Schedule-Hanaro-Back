package com.hanaro.schedule_hanaro.customer.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.customer.dto.response.CustomerInfoResponse;
import com.hanaro.schedule_hanaro.customer.service.CustomerService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Customer", description = "고객 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
	private final CustomerService customerService;

	@GetMapping("")
	public ResponseEntity<CustomerInfoResponse> getCustomerInfo(Authentication authentication) {
		return ResponseEntity.ok()
			.body(customerService.findCustomer(authentication));
	}
}
