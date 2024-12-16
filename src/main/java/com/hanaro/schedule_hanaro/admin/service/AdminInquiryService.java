package com.hanaro.schedule_hanaro.admin.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.admin.dto.request.AdminInquiryListRequest;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryDetailResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryListResponse;
import com.hanaro.schedule_hanaro.admin.repository.AdminInquiryRepository;
import com.hanaro.schedule_hanaro.admin.repository.AdminInquiryResponseRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.InquiryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminInquiryService {

	private final AdminInquiryRepository adminInquiryRepository;
	private final AdminInquiryResponseRepository adminInquiryResponseRepository;

	public AdminInquiryListResponse findInquiryList(AdminInquiryListRequest request) {
		Pageable pageable = PageRequest.of(request.page(), request.size());

		Page<Inquiry> inquiries = adminInquiryRepository.findFilteredInquiries(
			request.status(),
			request.category().toString(),
			request.searchContent(),
			pageable
		);

		List<AdminInquiryListResponse.InquiryData> inquiryDataList = inquiries.getContent().stream()
			.map(inquiry -> AdminInquiryListResponse.InquiryData.from(
				inquiry,
				inquiry.getCustomer().getName()
			))
			.collect(Collectors.toList());

		return AdminInquiryListResponse.from(
			inquiryDataList,
			inquiries.getNumber(),
			inquiries.getSize(),
			inquiries.getTotalElements(),
			inquiries.getTotalPages()
		);
	}

	public AdminInquiryDetailResponse findInquiryDetail(Long inquiryId) {
		Inquiry inquiry = adminInquiryRepository.findInquiryDetailById(inquiryId)
			.orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다. ID: " + inquiryId));

		Customer customer = inquiry.getCustomer();
		Optional<InquiryResponse> inquiryResponse = adminInquiryResponseRepository.findByInquiryId(inquiryId);

		return AdminInquiryDetailResponse.of(
			inquiry.getId(),
			inquiry.getContent(),
			inquiry.getCategory().toString(),
			inquiry.getTags(),
			inquiry.getCreatedAt(),
			inquiryResponse.map(InquiryResponse::getCreatedAt).orElse(null),
			customer.getName(),
			customer.getPhoneNum(),
			inquiryResponse.map(InquiryResponse::getContent).orElse(null)
		);
	}
}
