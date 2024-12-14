package com.hanaro.schedule_hanaro.customer.service;

import com.hanaro.schedule_hanaro.customer.dto.request.InquiryListRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.customer.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {
	private final InquiryRepository inquiryRepository;

	public InquiryListResponse getInquiries(InquiryListRequest request) {
		Status status = Status.valueOf(request.status().toUpperCase());
		Integer page = request.page() != null ? request.page() : 1;
		Integer size = request.size() != null ? request.size() : 5;

		List inquiries = inquiryRepository.findByStatus(status);
		int start = (page - 1) * size;
		int end = Math.min(start + size, inquiries.size());
		List<Inquiry> pagedInquiries = inquiries.subList(start, end);

		List<InquiryResponse> inquiryResponses = pagedInquiries.stream()
			.map(inquiry -> InquiryResponse.of(
				inquiry.getId(),
				null,
				inquiry.getCategory().toString(),
				inquiry.getInquiryStatus().getInquiryStatus(),
				inquiry.getContent(),
				List.of(inquiry.getTags().split(","))
			))
			.toList();

		int totalItems = inquiryRepository.countByStatus(status);
		int totalPages = (int) Math.ceil((double) totalItems / size);

		return InquiryListResponse.of(
			inquiryResponses,
			page,
			size,
			totalItems,
			totalPages
		);
	}
}
