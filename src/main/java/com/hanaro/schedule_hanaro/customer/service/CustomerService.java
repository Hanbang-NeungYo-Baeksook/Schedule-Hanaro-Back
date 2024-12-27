package com.hanaro.schedule_hanaro.customer.service;

import java.time.LocalDate;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.response.CustomerInfoResponse;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import com.hanaro.schedule_hanaro.global.repository.CallRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.global.repository.VisitRepository;
import com.hanaro.schedule_hanaro.global.utils.PrincipalUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
	private final CustomerRepository customerRepository;
	private final VisitRepository visitRepository;
	private final CallRepository callRepository;
	private final InquiryRepository inquiryRepository;

	public CustomerInfoResponse findCustomer(Authentication authentication) {
		Long id = PrincipalUtils.getId(authentication);
		Customer customer = customerRepository.findById(id).orElseThrow();
		Integer callAmount = callRepository.countCallsByCustomerAndStatusNotIn(customer, Status.CANCELED);
		Integer inquiryAmount = inquiryRepository.countInquiryByCustomer(customer);
		Integer visitAmount = visitRepository.countByCustomerAndVisitDateAndStatus(customer, LocalDate.now(),
			Status.PENDING);
		return CustomerInfoResponse.of(customer.getName(), customer.getAuthId(), customer.getBirth().toString(),
			customer.getPhoneNum(), callAmount, inquiryAmount, visitAmount);
	}
}
