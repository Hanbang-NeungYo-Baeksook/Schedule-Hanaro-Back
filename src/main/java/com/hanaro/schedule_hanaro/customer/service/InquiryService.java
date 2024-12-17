package com.hanaro.schedule_hanaro.customer.service;

import com.hanaro.schedule_hanaro.customer.dto.request.InquiryCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryCreateResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryReplyDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.customer.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

	// 1:1 상담 예약
	@Transactional
	public InquiryCreateResponse createInquiry(Long customerId, InquiryCreateRequest request) {
		Customer customer = customerRepository.findById(customerId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID입니다."));

		int maxInquiryNum = inquiryRepository.findMaxInquiryNum();

		// 번호 추가
		int newInquiryNum = maxInquiryNum + 1;

		Inquiry inquiry = Inquiry.builder()
			.customer(customer)
			.content(request.content())
			.inquiryNum(newInquiryNum)
			.category(request.category())
			.tags("default")
			.build();

		Inquiry savedInquiry = inquiryRepository.save(inquiry);

		return InquiryCreateResponse.builder()
			.inquiryId(savedInquiry.getId())
			.build();
	}

	// 1:1 상담 목록 조회
	public InquiryListResponse getInquiryList(String status, int page, int size) {
		InquiryStatus inquiryStatus = InquiryStatus.valueOf(status.toUpperCase());
		Pageable pageable = PageRequest.of(page - 1, size);

		Slice<Inquiry> inquirySlice = inquiryRepository.findByInquiryStatus(inquiryStatus, pageable);

		List<InquiryResponse> inquiryList = inquirySlice.getContent().stream()
			.map(inquiry -> InquiryResponse.builder()
				.inquiryId(inquiry.getId())
				.inquiryNum(inquiry.getInquiryNum())
				.category(inquiry.getCategory().name())
				.status(inquiry.getInquiryStatus().getInquiryStatus())
				.content(inquiry.getContent())
				.tags(List.of(inquiry.getTags().split(",")))
				.build())
			.toList();

		InquiryListResponse.Pagination pagination = InquiryListResponse.Pagination.builder()
			.currentPage(page)
			.pageSize(size)
			.hasNext(inquirySlice.hasNext())
			.build();

		return InquiryListResponse.builder()
			.inquiryList(inquiryList)
			.pagination(pagination)
			.build();
	}

	// 1:1 상담 상세
	public InquiryResponse getInquiryDetail(Long inquiryId) {
		Inquiry inquiry = inquiryRepository.findById(inquiryId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담 ID입니다."));

		return InquiryResponse.builder()
			.inquiryId(inquiry.getId())
			.inquiryNum(inquiry.getInquiryNum())
			.category(inquiry.getCategory().name())
			.status(inquiry.getInquiryStatus().name().toLowerCase())
			.content(inquiry.getContent())
			.tags(List.of(inquiry.getTags().split(",")))
			.build();
	}

	// 1:1 상담 답변 상세
	public InquiryReplyDetailResponse getInquiryReplyDetail(Long inquiryId) {
		Inquiry inquiry = inquiryRepository.findById(inquiryId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담 ID입니다."));

		return InquiryReplyDetailResponse.builder()
			.content(inquiry.getContent())
			.build();
	}

	// 1:1 상담 취소
	@Transactional
	public void cancelInquiry(Long inquiryId) {
		Inquiry inquiry = inquiryRepository.findById(inquiryId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담 ID입니다."));
		inquiryRepository.delete(inquiry);
	}
}
