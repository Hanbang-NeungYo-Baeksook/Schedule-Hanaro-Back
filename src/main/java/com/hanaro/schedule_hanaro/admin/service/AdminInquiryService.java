package com.hanaro.schedule_hanaro.admin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.admin.dto.request.AdminInquiryListRequest;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryDetailResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryListResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryResponse;
import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.InquiryResponseRepository;
import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.InquiryResponse;
import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.global.utils.PrincipalUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminInquiryService {

	private final InquiryResponseRepository inquiryResponseRepository;
	private final AdminRepository adminRepository;
	private final InquiryRepository inquiryRepository;

	public AdminInquiryListResponse findInquiryList(AdminInquiryListRequest request) {
		Pageable pageable = PageRequest.of(request.page() - 1, request.size(), Sort.by(Sort.Direction.ASC, "id"));


		Page<Inquiry> inquiries = inquiryRepository.findFilteredInquiries(
			request.inquiryStatus(),
			request.category(),
			request.searchContent(),
			pageable
		);

		if (inquiries.isEmpty()) {
			return AdminInquiryListResponse.from(
				List.of(), // 빈 목록
				request.page(),
				request.size(),
				0L,        // 총 아이템 수
				0          // 총 페이지 수
			);
		}




		List<AdminInquiryListResponse.InquiryData> inquiryDataList = inquiries.getContent().stream()
			.map(inquiry -> AdminInquiryListResponse.InquiryData.from(
				inquiry,
				inquiry.getCustomer().getName()
			))
			.collect(Collectors.toList());

		return AdminInquiryListResponse.from(
			inquiryDataList,
			inquiries.getNumber() + 1,
			inquiries.getSize(),
			inquiries.getTotalElements(),
			inquiries.getTotalPages()
		);
	}

	public AdminInquiryDetailResponse findInquiryDetail(Long inquiryId) {
		Inquiry inquiry = inquiryRepository.findInquiryDetailById(inquiryId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_INQUIRY));

		Customer customer = inquiry.getCustomer();
		// 이 부분 반드시 리팩토링
		Optional<InquiryResponse> inquiryResponse = inquiryResponseRepository.findByInquiryId(inquiryId);

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

	@Transactional
	public AdminInquiryResponse registerInquiryResponse(Long inquiryId, String content, Authentication authentication) {
		// 입력값 검증
		if (content == null || content.trim().isEmpty()) {
			throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "답변 내용이 비어있습니다.");
		}
		if (content.length() > 500) {
			throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "답변 내용이 500자를 초과했습니다.");
		}

		Inquiry inquiry = inquiryRepository.findById(inquiryId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_INQUIRY));

		Admin admin = adminRepository.findById(PrincipalUtils.getId(authentication))
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_ADMIN));

		InquiryResponse response = inquiryResponseRepository.findByInquiryId(inquiryId).orElse(null);

		if (response != null) {
			throw new GlobalException(ErrorCode.ALREADY_POST_RESPONSE);
		}

		InquiryResponse inquiryResponse = InquiryResponse.builder()
			.inquiry(inquiry)
			.admin(admin)
			.content(content)
			.createdAt(LocalDateTime.now())
			.build();

		InquiryResponse savedResponse = inquiryResponseRepository.save(inquiryResponse);

		inquiry.setStatus(InquiryStatus.REGISTRATIONCOMPLETE);
		inquiryRepository.save(inquiry);

		return AdminInquiryResponse.of(
			savedResponse.getInquiry().getId(),
			savedResponse.getAdmin().getId(),
			savedResponse.getContent(),
			savedResponse.getCreatedAt()
		);
	}
}
