package com.hanaro.schedule_hanaro.customer.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.customer.dto.request.CallRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.CallDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallResponse;
import com.hanaro.schedule_hanaro.global.auth.info.UserInfo;
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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
public class CallService {

	private final CallRepository callRepository;
	private final CustomerRepository customerRepository;

	private static final int MAX_RESERVATION = 60;
	private static final int CONSULTANTS = 25;
	private static final int CONSULTATION_TIME_MINUTE = 15;

	@Transactional
	public CallResponse createCall(Authentication authentication, CallRequest request) throws InterruptedException {
		Customer customer = customerRepository.findById(PrincipalUtils.getId(authentication))
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CUSTOMER));

		// 시간대 범위 설정 (30분 간격)
		LocalDateTime[] timeSlotRange = getTimeSlotRange(request.callDate());
		LocalDateTime startTime = timeSlotRange[0];
		LocalDateTime endTime = timeSlotRange[1];


		while (true) {
			try {
				int newCallNum = generateNextCallNum(startTime, endTime);

				if (newCallNum >= MAX_RESERVATION) {
					throw new GlobalException(ErrorCode.FULL_CALL_RESERVATION);
				}

				Call newCall = Call.builder()
					.customer(customer)
					.callDate(request.callDate())
					.callNum(newCallNum)
					.category(Category.valueOf(request.category().toUpperCase()))
					.content(request.content())
					.tags("default")
					.build();

				Call savedCall = callRepository.save(newCall);

				return CallResponse.builder()
					.callId(savedCall.getId())
					.build();

			} catch (OptimisticLockingFailureException ex) {
				System.out.println(Thread.currentThread().getName() + " : " + ex.getMessage());
				Thread.sleep(100);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 5)
	public int generateNextCallNum(LocalDateTime startTime, LocalDateTime endTime) {
		Integer maxCallNum = callRepository.findMaxCallNumByCallDateBetweenForUpdate(startTime, endTime);
		return (maxCallNum == null ? 0 : maxCallNum) + 1;
	}


	@Transactional
	public void cancelCall(Long callId) {
		Call call = callRepository.findById(callId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CALL));

		if (call.getStatus() != Status.PENDING) {
			throw new GlobalException(ErrorCode.WRONG_CALL_STATUS, "진행 중이거나 완료된 상담은 취소할 수 없습니다.");
		}

		call.setStatus(Status.CANCELED);
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
					.category(call.getCategory().toString())
					.status(call.getStatus().toString())
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
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CALL));

		Map<String, Integer> waitInfo = calculateWaitInfo(call);

		return CallDetailResponse.builder()
			.callId(call.getId())
			.customerName(call.getCustomer().getName())
			.callDate(call.getCallDate().toLocalDate().toString())
			.callTime(call.getCallDate().toLocalTime().toString() + "Z")
			.callNum(call.getCallNum())
			.category(call.getCategory().toString())
			.status(call.getStatus().toString())
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
