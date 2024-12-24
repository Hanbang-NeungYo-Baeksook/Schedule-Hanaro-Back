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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallDto;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInquiryListResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryDto;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCustomerService {

    private final CustomerRepository customerRepository;
    private final CallRepository callRepository;
    private final InquiryRepository inquiryRepository;


    public AdminCustomerInfoResponse findCustomerInfoById(Long customerId) {
        if (customerId == null) {
            throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "고객 ID가 필요합니다.");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CUSTOMER));
        
        return AdminCustomerInfoResponse.from(customer);
    }

    public AdminCustomerInquiryListResponse findCustomerInquiryList(Long customerId) {
        if (customerId == null) {
            throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "고객 ID가 필요합니다.");
        }

        // 고객 존재 여부 확인
        if (!customerRepository.existsById(customerId)) {
            throw new GlobalException(ErrorCode.NOT_FOUND_CUSTOMER);
        }

        List<Call> callList = callRepository.findByCustomerId(customerId);
        List<Inquiry> inquiryList = inquiryRepository.findAllByCustomerId(customerId);

        List<AdminCallDto> callDtos = callList.stream()
                .map(call -> {
                    if (call.getCategory() == null) {
                        throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "전화 상담 카테고리 정보가 없습니다.");
                    }
                    if (call.getStatus() == null) {
                        throw new GlobalException(ErrorCode.WRONG_CALL_STATUS);
                    }
                    return AdminCallDto.of(
                            call.getId(),
                            call.getCallDate(),
                            call.getCallNum(),
                            call.getCategory().toString(),
                            call.getStatus().toString(),
                            call.getContent(),
                            call.getStartedAt(),
                            call.getEndedAt()
                    );
                })
                .collect(Collectors.toList());

        List<AdminInquiryDto> inquiryDtos = inquiryList.stream()
                .map(inquiry -> {
                    if (inquiry.getCategory() == null) {
                        throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "문의 카테고리 정보가 없습니다.");
                    }
                    if (inquiry.getInquiryStatus() == null) {
                        throw new GlobalException(ErrorCode.WRONG_INQUIRY_STATUS);
                    }
                    return AdminInquiryDto.of(
                            inquiry.getId(),
                            inquiry.getContent(),
                            inquiry.getCategory().toString(),
                            inquiry.getInquiryStatus().toString(),
                            inquiry.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());

        return AdminCustomerInquiryListResponse.of(callDtos, inquiryDtos);
    }

    public AdminCustomerListResponse getCustomerList(int page, int size) {
        if (page < 1) {
            throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "페이지 번호는 1 이상이어야 합니다.");
        }
        if (size < 1) {
            throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "페이지 크기는 1 이상이어야 합니다.");
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Customer> customers = customerRepository.findAll(pageable);

        if (customers.isEmpty()) {
            throw new GlobalException(ErrorCode.NOT_FOUND_CUSTOMER, "고객 목록이 비어있습니다.");
        }

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
