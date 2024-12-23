package com.hanaro.schedule_hanaro.customer.service;

import com.hanaro.schedule_hanaro.customer.dto.request.InquiryCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryCreateResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryReplyDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;
import com.hanaro.schedule_hanaro.global.repository.InquiryResponseRepository;
import com.hanaro.schedule_hanaro.global.utils.PrincipalUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {
	private final InquiryRepository inquiryRepository;
	private final CustomerRepository customerRepository;
	private final InquiryResponseRepository inquiryResponseRepository;

	// 1:1 상담 예약
	@Transactional
	public InquiryCreateResponse createInquiry(Authentication authentication, InquiryCreateRequest request) {
		Customer customer = customerRepository.findById(PrincipalUtils.getId(authentication))
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CUSTOMER));

		int maxInquiryNum = inquiryRepository.findMaxInquiryNum();

		// 번호 추가
		int newInquiryNum = maxInquiryNum + 1;

		Inquiry inquiry = Inquiry.builder()
			.customer(customer)
			.content(request.content())
			.inquiryNum(newInquiryNum)
			.category(category)
			.status(InquiryStatus.PENDING)
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

		List<InquiryListResponse.InquiryData> inquiryDataList = inquirySlice.getContent().stream()
			.map(inquiry -> InquiryListResponse.InquiryData.builder()
				.inquiryId(inquiry.getId())
				.inquiryNum(inquiry.getInquiryNum())
				.category(inquiry.getCategory().name())
				.status(inquiry.getInquiryStatus().name().toLowerCase())
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
			.data(inquiryDataList)
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
			.category(inquiry.getCategory().toString())
			.status(inquiry.getInquiryStatus().toString())
			.content(inquiry.getContent())
			.tags(List.of(inquiry.getTags().split(",")))
			.build();
	}

	// 1:1 상담 답변 상세
	public InquiryReplyDetailResponse getInquiryReplyDetail(Long inquiryId) {
		Inquiry inquiry = inquiryRepository.findById(inquiryId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담 ID입니다."));

		com.hanaro.schedule_hanaro.global.domain.InquiryResponse inquiryResponse =
			inquiryResponseRepository.findByInquiry(inquiry).orElse(null);

		String replyContent = inquiryResponse != null ? inquiryResponse.getContent() : "";

		return InquiryReplyDetailResponse.builder()
			.content(inquiry.getContent())
			.status(inquiry.getInquiryStatus().toString())
			.reply(replyContent)
			.tag(List.of(inquiry.getTags().split(",")))
			.build();
	}

	// 1:1 상담 취소
	@Transactional
	public void cancelInquiry(Long inquiryId) {
		Inquiry inquiry = inquiryRepository.findById(inquiryId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_INQUIRY));
		// InquiryResponse 삭제 (답변이 있을 경우)
		inquiryResponseRepository.findByInquiry(inquiry).ifPresent(inquiryResponseRepository::delete);
		inquiryRepository.delete(inquiry);
	}
}
