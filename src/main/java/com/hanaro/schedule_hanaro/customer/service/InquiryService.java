package com.hanaro.schedule_hanaro.customer.service;

import com.hanaro.schedule_hanaro.customer.dto.request.InquiryCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryCreateResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryReplyDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;
import com.hanaro.schedule_hanaro.global.repository.InquiryResponseRepository;
import com.hanaro.schedule_hanaro.global.service.RecommendService;
import com.hanaro.schedule_hanaro.global.utils.PrincipalUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.hanaro.schedule_hanaro.global.utils.TagRecommender.recommendTagsForQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InquiryService {
	private final InquiryRepository inquiryRepository;
	private final CustomerRepository customerRepository;
	private final InquiryResponseRepository inquiryResponseRepository;
	private final AdminRepository adminRepository;
	private final RecommendService recommendService;

	// 1:1 상담 예약
	@Transactional
	public InquiryCreateResponse createInquiry(Authentication authentication, InquiryCreateRequest request) {
		Customer customer = customerRepository.findById(PrincipalUtils.getId(authentication))
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CUSTOMER));

		Category category = Category.fromCategoryName(request.category());

		int maxInquiryNum = inquiryRepository.findMaxInquiryNum();

		// 번호 추가
		int newInquiryNum = maxInquiryNum + 1;

		List<String> tags = recommendTagsForQuery(request.content());

		// 상담 할당받을 admin 구하기
		Optional<Inquiry> inquiry = inquiryRepository.findFirstByOrderByIdDesc();

		Admin admin = (inquiry.isPresent())
			? adminRepository.findFirstByIdGreaterThanOrderByIdAsc(
				inquiry.get().getAdmin().getId())
			.orElseGet(() -> adminRepository.findFirstByOrderByIdAsc().orElse(null))
			: adminRepository.findFirstByOrderByIdAsc().orElse(null);

		// inquiry 등록
		Inquiry savedInquiry = inquiryRepository.save(Inquiry.builder()
			.customer(customer)
			.admin(admin)
			.content(request.content())
			.inquiryNum(newInquiryNum)
			.category(category)
			.status(InquiryStatus.PENDING)
			.tags(String.join(",", tags))
			// TODO 여기 문자열로 처리해야되는데 급해서 toString()으로 함. 적절하게 바꿔주세요
			.queryVector(recommendService.getQueryVector(request.content()).toString())
			.build());

		// inquiryResponse 등록
		inquiryResponseRepository.save(com.hanaro.schedule_hanaro.global.domain.InquiryResponse.builder()
			.inquiry(savedInquiry)
			.admin(admin)
			.content("")
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build());

		return InquiryCreateResponse.builder()
			.inquiryId(savedInquiry.getId())
			.build();
	}

	// 1:1 상담 목록 조회
	public InquiryListResponse getInquiryList(Long customerId, String status, int page, int size) {
		InquiryStatus inquiryStatus = InquiryStatus.valueOf(status.toUpperCase());
		Pageable pageable = PageRequest.of(page - 1, size);

		Slice<Inquiry> inquirySlice = inquiryRepository.findByCustomerIdAndInquiryStatus(customerId, inquiryStatus, pageable);

		List<InquiryListResponse.InquiryData> inquiryDataList = inquirySlice.getContent().stream()
			.map(inquiry -> InquiryListResponse.InquiryData.builder()
				.inquiryId(inquiry.getId())
				.inquiryNum(inquiry.getInquiryNum())
				.category(inquiry.getCategory().toString())
				.status(inquiry.getInquiryStatus().toString())
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
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_INQUIRY));

		// 대기 중인 인수 구하기
		int waitingAmount = inquiryRepository.countByAdminIdAndInquiryStatus(inquiry.getAdmin().getId(), InquiryStatus.PENDING);

		System.out.println("waitingAmount = " + waitingAmount);

		return InquiryResponse.builder()
			.inquiryId(inquiry.getId())
			.inquiryNum(inquiry.getInquiryNum())
			.adminId(inquiry.getAdmin().getId())
			.category(inquiry.getCategory().toString())
			.status(inquiry.getInquiryStatus().toString())
			.customerName(inquiry.getCustomer().getName())
			.content(inquiry.getContent())
			.tags(List.of(inquiry.getTags().split(",")))
			.waitingAmount(waitingAmount)
			.build();
	}

	// 1:1 상담 답변 상세
	public InquiryReplyDetailResponse getInquiryReplyDetail(Long inquiryId) {
		Inquiry inquiry = inquiryRepository.findById(inquiryId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_INQUIRY));

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
