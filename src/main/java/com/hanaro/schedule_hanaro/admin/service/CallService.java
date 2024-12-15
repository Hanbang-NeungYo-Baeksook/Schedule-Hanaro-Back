package com.hanaro.schedule_hanaro.admin.service;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.admin.dto.response.CallHistoryResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.CallInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.CallWaitResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.InquiryHistoryResponse;
import com.hanaro.schedule_hanaro.admin.repository.AdminRepository;
import com.hanaro.schedule_hanaro.admin.repository.CallMemoRepository;
import com.hanaro.schedule_hanaro.admin.repository.CallRepository;
import com.hanaro.schedule_hanaro.customer.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.CallMemo;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallService {
	private final CallRepository callRepository;
	private final InquiryRepository inquiryRepository;
	private final CallMemoRepository callMemoRepository;
	private final AdminRepository adminRepository;

	public CallWaitResponse findWaitList() {

		Function<Call, CallInfoResponse> extractCallInfo = call -> CallInfoResponse.from(
			call,
			call.getCustomer(),
			callRepository.findCallHistoryByCustomerId(call.getCustomer().getId(), call.getId())
				.stream()
				.map(CallHistoryResponse::from)
				.toList(),
			inquiryRepository.findByCustomerId(call.getCustomer().getId())
				.stream()
				.map(InquiryHistoryResponse::from)
				.toList()
		);

		// 진행 중
		CallInfoResponse progressCall = callRepository.findByStatus(Status.PROGRESS)
			.stream()
			.map(extractCallInfo)
			.findFirst()
			.orElse(null);

		// 대기 중
		List<CallInfoResponse> pendingCalls = callRepository.findByStatus(Status.PENDING)
			.stream()
			.map(extractCallInfo)
			.toList();

		return CallWaitResponse.of(progressCall, pendingCalls);
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
}
