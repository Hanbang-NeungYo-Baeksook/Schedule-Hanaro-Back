package com.hanaro.schedule_hanaro.admin.service;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerListResponse;
import com.hanaro.schedule_hanaro.global.repository.CallRepository;
import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallDto;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInquiryListResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryDto;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCustomerService {

    private final CustomerRepository customerRepository;
    private final CallRepository callRepository;
    private final InquiryRepository inquiryRepository;


    public AdminCustomerInfoResponse findCustomerInfoById(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        return AdminCustomerInfoResponse.from(customer);
    }

    public AdminCustomerInquiryListResponse findCustomerInquiryList(Long customerId) {
        List<Call> callList = callRepository.findByCustomerId(customerId);

        List<Inquiry> inquiryList = inquiryRepository.findAllByCustomerId(customerId);

        List<AdminCallDto> callDtos = callList.stream()
                .map(call -> AdminCallDto.of(
                        call.getId(),
                        call.getCallDate(),
                        call.getCallNum(),
                        call.getCategory().toString(),
                        call.getStatus().toString(),
                        call.getContent(),
                        call.getStartedAt(),
                        call.getEndedAt()
                ))
                .collect(Collectors.toList());

        List<AdminInquiryDto> inquiryDtos = inquiryList.stream()
                .map(inquiry -> AdminInquiryDto.of(
                        inquiry.getId(),
                        inquiry.getContent(),
                        inquiry.getCategory().toString(),
                        inquiry.getInquiryStatus().toString(),
                        inquiry.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return AdminCustomerInquiryListResponse.of(callDtos, inquiryDtos);
    }

    public AdminCustomerListResponse getCustomerList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Customer> customers = customerRepository.findAll(pageable);

        return AdminCustomerListResponse.from(
            customers.getContent().stream()
                .map(AdminCustomerListResponse.CustomerData::of)
                .toList(),
            customers.getNumber() + 1,
            customers.getSize(),
            customers.getTotalElements(),
            customers.getTotalPages()
        );
    }

}
