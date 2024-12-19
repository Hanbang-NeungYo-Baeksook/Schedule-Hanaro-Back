package com.hanaro.schedule_hanaro.customer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.customer.dto.request.CallRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.CallDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallResponse;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.CallRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import com.hanaro.schedule_hanaro.global.utils.PrincipalUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallService {

	private final CallRepository callRepository;
	private final CustomerRepository customerRepository;

	private static final int MAX_RESERVATION_PERSON = 50;
	private static final int MAX_RESERVATION_TICKET = 70;
	private static final int CONSULTANTS = 25;
	private static final int CONSULTATION_TIME_MINUTE = 15;

	@Transactional
	public CallResponse createCall(Authentication authentication, CallRequest request) {

		Customer customer = customerRepository.findById(PrincipalUtils.getId(authentication))
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원 id입니다."));

		// 시간대 범위 설정 (30분 간격)
		LocalDateTime[] timeSlotRange = getTimeSlotRange(request.callDate());
		LocalDateTime startTime = timeSlotRange[0];
		LocalDateTime endTime = timeSlotRange[1];

		// 해당 시간대의 예약 수 조회
		int pendingCalls = callRepository.countByTimeSlotAndStatus(startTime, endTime, Status.PENDING);

		if (pendingCalls >= MAX_RESERVATION_TICKET) {
			throw new IllegalStateException("해당 시간대의 예약이 모두 찼습니다.");
		}

		int newCallNum = pendingCalls + 1;

		Call call = Call.builder()
			.customer(customer)
			.callDate(request.callDate())
			.callNum(newCallNum)
			.category(Category.valueOf(request.category().toUpperCase()))
			.content(request.content())
			.tags("default")
			.build();

		Call savedCall = callRepository.save(call);

		return CallResponse.builder()
			.callId(savedCall.getId())
			.build();
	}


	@Transactional
	public void cancelCall(Long callId) {
		Call call = callRepository.findById(callId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CALL));

		if (call.getStatus() != Status.PENDING) {
			throw new GlobalException(ErrorCode.WRONG_CALL_STATUS, "진행 중이거나 완료된 상담은 취소할 수 없습니다.");
		}

		call.setStatus(Status.CANCELLED);
		callRepository.save(call);
	}

	private LocalDateTime[] getTimeSlotRange(LocalDateTime callDateTime) {
		int minute = callDateTime.getMinute() < 30 ? 0 : 30;
		LocalDateTime startTime = callDateTime.withMinute(minute).withSecond(0).withNano(0);
		LocalDateTime endTime = startTime.plusMinutes(30).minusSeconds(1);
		return new LocalDateTime[]{startTime, endTime};
	}

	public CallListResponse getCallList(String status, int page, int size) {
		Status callStatus = Status.valueOf(status.toUpperCase());
		Pageable pageable = PageRequest.of(page - 1, size);

		Slice<Call> callSlice = callRepository.findByStatus(callStatus, pageable);

		List<CallListResponse.CallData> callDataList = callSlice.getContent().stream()
			.map(call -> {
				Map<String, Integer> waitInfo = calculateWaitInfo(call);

				return CallListResponse.CallData.builder()
					.callId(call.getId())
					.callDate(call.getCallDate().toLocalDate().toString())
					.callTime(call.getCallDate().toLocalTime().toString())
					.callNum(call.getCallNum())
					.category(call.getCategory().name())
					.status(call.getStatus().getStatus())
					.waitNum(waitInfo.get("waitNum"))
					.estimatedWaitTime(waitInfo.get("estimatedWaitTime"))
					.build();
			})
			.toList();

		CallListResponse.Pagination pagination = CallListResponse.Pagination.builder()
			.currentPage(page)
			.pageSize(size)
			.hasNext(callSlice.hasNext())
			.build();

		return CallListResponse.builder()
			.data(callDataList)
			.pagination(pagination)
			.build();
	}

	public CallDetailResponse getCallDetail(Long callId) {
		Call call = callRepository.findById(callId)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상담 id입니다."));

		Map<String, Integer> waitInfo = calculateWaitInfo(call);

		return CallDetailResponse.builder()
			.callId(call.getId())
			.customerName(call.getCustomer().getName())
			.callDate(call.getCallDate().toLocalDate().toString())
			.callTime(call.getCallDate().toLocalTime().toString() + "Z")
			.callNum(call.getCallNum())
			.category(call.getCategory().name())
			.status(call.getStatus().getStatus())
			.content(call.getContent())
			.tags(List.of(call.getTags().split(",")))
			.waitNum(waitInfo.get("waitNum"))
			.estimatedWaitTime(waitInfo.get("estimatedWaitTime"))
			.build();
	}

	private Map<String, Integer> calculateWaitInfo(Call call) {
		if (call.getStatus() != Status.PENDING) {
			return Map.of(
				"waitNum", 0,
				"estimatedWaitTime", 0
			);
		}

		int waitNum = (call.getCallNum() - 1) / CONSULTANTS;
		int estimatedWaitTime = waitNum * CONSULTATION_TIME_MINUTE;

		return Map.of(
			"waitNum", waitNum,
			"estimatedWaitTime", estimatedWaitTime
		);
	}

}
