package com.hanaro.schedule_hanaro.customer.service;

import com.hanaro.schedule_hanaro.customer.dto.request.InquiryCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.request.InquiryListRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryCreateResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.customer.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {
	private final InquiryRepository inquiryRepository;
	private final CustomerRepository customerRepository;

	// 1:1 상담 목록
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

	// 1:1 상담 상세
	public InquiryResponse getInquiryDetail(Long inquiryId) {
		Inquiry inquiry = inquiryRepository.findById(inquiryId).orElse(null);

		if (inquiry == null) {
			throw new RuntimeException("존재하지 않는 사용자 ID입니다");
		}

		return InquiryResponse.of(
			inquiry.getId(),
			null,
			inquiry.getCategory().toString(),
			inquiry.getInquiryStatus().getInquiryStatus(),
			inquiry.getContent(),
			List.of(inquiry.getTags().split(","))
		);
	}

	// 1:1 상담 답변 상세
	public String getInquiryReply(Long inquiryId) {
		Inquiry inquiry = inquiryRepository.findById(inquiryId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다."));
		return inquiry.getContent();
	}

	@Transactional
	public InquiryCreateResponse createInquiry(Long customerId, InquiryCreateRequest request) {

		Customer customer = customerRepository.findById(customerId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다."));

		Inquiry inquiry = Inquiry.builder()
			.customer(customer)
			.category(request.category())
			.content(request.content())
			.createdAt(LocalDateTime.now())
			.tags("default")
			.build();

		Inquiry savedInquiry = inquiryRepository.save(inquiry);

		return InquiryCreateResponse.builder()
			.inquiryId(savedInquiry.getId())
			.build();
	}
}
