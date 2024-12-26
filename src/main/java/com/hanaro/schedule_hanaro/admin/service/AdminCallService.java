package com.hanaro.schedule_hanaro.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallDetailResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallHistoryListResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallHistoryResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallWaitResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryHistoryResponse;

import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.CallMemo;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import com.hanaro.schedule_hanaro.global.repository.CallMemoRepository;
import com.hanaro.schedule_hanaro.global.repository.CallRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.CallMemo;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import com.hanaro.schedule_hanaro.global.utils.PrincipalUtils;
import com.hanaro.schedule_hanaro.global.websocket.handler.WebsocketHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminCallService {
	private final CallRepository callRepository;
	private final InquiryRepository inquiryRepository;
	private final CallMemoRepository callMemoRepository;
	private final AdminRepository adminRepository;
	private final CustomerRepository customerRepository;

	private final WebsocketHandler websocketHandler;

	public AdminCallWaitResponse findWaitList(String date, String time, Authentication authentication) {

		Admin admin = adminRepository.findById(PrincipalUtils.getId(authentication))
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_ADMIN));

		// 진행 중
		AdminCallInfoResponse progressCall = callRepository.findByStatusAndAdminId(Status.PROGRESS, admin.getId())
			.stream()
			.findFirst()
			.map((call) -> {
				System.out.println(call.getId());
				return AdminCallInfoResponse.from(call, callMemoRepository.findByCallId(call.getId()));
			})
			.orElse(null);

		// 시간대 구하기
		LocalDateTime targetDateTimeStart = null;
		LocalDateTime targetDateTimeEnd = null;

		if (date != null) {
			LocalDate targetDate = LocalDate.parse(date);

			if (time != null) {
				LocalTime targetTime = LocalTime.parse(time);
				targetDateTimeStart = LocalDateTime.of(targetDate, targetTime);
				targetDateTimeEnd = targetDateTimeStart.plusMinutes(30);
			} else {
				targetDateTimeStart = targetDate.atStartOfDay();
				targetDateTimeEnd = targetDate.plusDays(1).atStartOfDay();
			}
		}

		// 대기 중
		List<AdminCallInfoResponse> pendingCalls = callRepository.findPendingCallsByDateTimeRange(targetDateTimeStart, targetDateTimeEnd)
			.stream()
			.map(call -> AdminCallInfoResponse.from(call, callMemoRepository.findByCallId(call.getId())))
			.toList();

		return AdminCallWaitResponse.of(progressCall, pendingCalls);
	}

	@Transactional
	public Long changeCallStatusProgress(Authentication authentication) {
		// 상담 진행 중으로 변경

		Admin admin = adminRepository.findById(PrincipalUtils.getId(authentication))
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_ADMIN));

		// 진행 중인 상담 없는지 확인
		List<Call> calls = callRepository.findByStatusAndAdminId(Status.PROGRESS, admin.getId());

		if (!calls.isEmpty()) {
			throw new GlobalException(ErrorCode.ALREADY_PROGRESS_COUNSLATION);
		}

		// 대기 번호가 가장 빠른 상담 조회 (비관적 락)
		Call call = callRepository.findFirstByStatusOrderByCallNumAsc(Status.PENDING)
			.orElseThrow(() -> new GlobalException(ErrorCode.EMPTY_WAITS));

		// call의 상태를 progress로 변경
		callRepository.updateStatusWithStartedAt(call.getId(), Status.PROGRESS, LocalDateTime.now());

		String message = "관리자가 새로운 상담을 시작했습니다: 상담 ID " + call.getId();
		websocketHandler.notifySubscribers(1L, message);

		// call memo 빈 문자열로 등록
		callMemoRepository.save(CallMemo.builder()
			.call(call)
			.admin(admin)
			.content("")
			.build());
		return call.getId();

	}

	@Transactional
	public Long changeCallStatusComplete(Long callId) {
		// 상담 완료로 변경
		Call call = callRepository.findById(callId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CALL));

		if (call.getStatus().equals(Status.PROGRESS)) {
			callRepository.updateStatusWithEndedAt(callId, Status.COMPLETE, LocalDateTime.now());
			return callId;
		} else {
			throw new GlobalException(ErrorCode.WRONG_CALL_STATUS);
		}

	}

	@Transactional
	public Long saveCallMemo(Authentication authentication, Long callId, String content) {
		Call call = callRepository.findById(callId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CALL));

		CallMemo callMemo = callMemoRepository.findByCallId(callId);

		if (callMemo != null) {
			if (!callMemo.getContent().isEmpty()) {
				throw new GlobalException(ErrorCode.ALREADY_POST_MEMO);
			}

			CallMemo updatedCallMemo = CallMemo.builder()
				.id(callMemo.getId())
				.call(callMemo.getCall())
				.admin(callMemo.getAdmin())
				.content(content)
				.build();

			callMemoRepository.save(updatedCallMemo);
		} else {
			Admin admin = adminRepository.findById(PrincipalUtils.getId(authentication))
				.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_ADMIN));

			CallMemo newCallMemo = CallMemo.builder()
				.call(call)
				.admin(admin)
				.content(content)
				.build();

			callMemoRepository.save(newCallMemo);
		}


		return callId;
	}

	public AdminCallHistoryListResponse findFilteredCalls(int page, int size, Status status, LocalDateTime startedAt, LocalDateTime endedAt, Category category, String keyword) {
		Pageable pageable = PageRequest.of(page - 1, size);


		Slice<Call> callSlice = callRepository.findByFiltering(pageable, status, startedAt, endedAt, category, keyword);

		List<AdminCallHistoryResponse> callDataList = callSlice.getContent().stream()
			.map(call -> AdminCallHistoryResponse.builder()
				.id(call.getId())
				.content(call.getContent())
				.category(call.getCategory().toString())
				.build())
			.toList();

		AdminCallHistoryListResponse.Pagination pagination = AdminCallHistoryListResponse.Pagination.builder()
			.currentPage(page)
			.pageSize(size)
			.hasNext(callSlice.hasNext())
			.build();

		return AdminCallHistoryListResponse.builder()
			.totalItems(callDataList.size())
			.data(callDataList)
			.pagination(pagination)
			.build();
	}

	public AdminCallDetailResponse findCall(Long callId) {
		Call call = callRepository.findById(callId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CALL));

		Customer customer = customerRepository.findById(call.getCustomer().getId())
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CUSTOMER));

		CallMemo callMemo = callMemoRepository.findByCallId(callId);

		if (callMemo == null) {
			return AdminCallDetailResponse.from(call, customer, null);
		}

		return AdminCallDetailResponse.from(call, customer, callMemo);
	}


	public AdminInquiryStatsDto getStatsByAdminId(Long adminId) {
		Object[] result = callRepository.findStatsByAdminId(adminId).get(0);
		return AdminInquiryStatsDto.of(
			((Number) result[0]).intValue(),
			((Number) result[1]).intValue(),
			((Number) result[2]).intValue(),
			((Number) result[3]).intValue()
		);
	}
}
