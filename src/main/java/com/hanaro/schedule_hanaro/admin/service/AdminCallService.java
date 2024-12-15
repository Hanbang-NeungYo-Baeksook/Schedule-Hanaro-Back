package com.hanaro.schedule_hanaro.admin.service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallDetailResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallHistoryListResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallHistoryResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallWaitResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryHistoryResponse;
import com.hanaro.schedule_hanaro.admin.repository.AdminRepository;
import com.hanaro.schedule_hanaro.admin.repository.CallMemoRepository;
import com.hanaro.schedule_hanaro.customer.repository.CallRepository;
import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.customer.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.CallMemo;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminCallService {
	private final CallRepository callRepository;
	private final InquiryRepository inquiryRepository;
	private final CallMemoRepository callMemoRepository;
	private final AdminRepository adminRepository;
	private final CustomerRepository customerRepository;

	public AdminCallWaitResponse findWaitList() {

		Function<Call, AdminCallInfoResponse> extractCallInfo = call -> AdminCallInfoResponse.from(
			call,
			call.getCustomer(),
			callRepository.findCallHistoryByCustomerId(call.getCustomer().getId(), call.getId())
				.stream()
				.map(AdminCallHistoryResponse::from)
				.toList(),
			inquiryRepository.findByCustomerId(call.getCustomer().getId())
				.stream()
				.map(AdminInquiryHistoryResponse::from)
				.toList()
		);

		// 진행 중
		AdminCallInfoResponse progressCall = callRepository.findByStatus(Status.PROGRESS)
			.stream()
			.map(extractCallInfo)
			.findFirst()
			.orElse(null);

		// 대기 중
		List<AdminCallInfoResponse> pendingCalls = callRepository.findByStatus(Status.PENDING)
			.stream()
			.map(extractCallInfo)
			.toList();

		return AdminCallWaitResponse.of(progressCall, pendingCalls);
	}

	@Transactional
	public String changeCallStatus(Long callId) {
		Call call = callRepository.findById(callId).orElseThrow(
			() -> new IllegalArgumentException("존재하지 않는 문의입니다.")
		);

		if (call.getStatus().equals(Status.PROGRESS)) {
			callRepository.updateStatus(callId, Status.COMPLETE);
		} else if (call.getStatus().equals(Status.PENDING)) {
			callRepository.updateStatus(callId, Status.PROGRESS);
		} else if (call.getStatus().equals(Status.COMPLETE)){
			throw new IllegalStateException("이미 완료된 상담입니다.");
		}

		return "Success";
	}

	@Transactional
	public String saveCallMemo(Long callId, String content) {
		Call call = callRepository.findById(callId).orElseThrow();
		// TODO: admin id 변경 -> security 연결
		Admin admin = adminRepository.findById(1).orElseThrow();

		callMemoRepository.save(
			CallMemo.builder()
				.call(call)
				.admin(admin)
				.content(content)
				.build()
		);

		return "Success";
	}

	public AdminCallHistoryListResponse findFilteredCalls(int page, int size, String status, LocalDate startedAt, LocalDate endedAt, Category category, String keyword) {
		Pageable pageable = PageRequest.of(page - 1, size);

		Slice<Call> callSlice = callRepository.findByFiltering(pageable, status, startedAt, endedAt, category, keyword);

		List<AdminCallHistoryResponse> callDataList = callSlice.getContent().stream()
			.map(call -> AdminCallHistoryResponse.builder()
				.id(call.getId())
				.content(call.getContent())
				.category(call.getCategory())
				.build())
			.toList();

		AdminCallHistoryListResponse.Pagination pagination = AdminCallHistoryListResponse.Pagination.builder()
			.currentPage(page)
			.pageSize(size)
			.hasNext(callSlice.hasNext())
			.build();

		return AdminCallHistoryListResponse.builder()
			.data(callDataList)
			.pagination(pagination)
			.build();
	}

	public AdminCallDetailResponse findCall (Long callId) {
		Call call = callRepository.findById(callId).orElseThrow(
			() -> new IllegalArgumentException("존재하지 않는 상담입니다.")
		);

		Customer customer = customerRepository.findById(6L).orElseThrow(
			() -> new IllegalArgumentException("존재하지 않는 고객입니다.")
		);

		return AdminCallDetailResponse.from(call, customer);
	}
}
