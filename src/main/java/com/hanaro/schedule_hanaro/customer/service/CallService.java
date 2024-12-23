package com.hanaro.schedule_hanaro.customer.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallInfoResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminCallService;
import com.hanaro.schedule_hanaro.customer.dto.request.CallRequest;
import com.hanaro.schedule_hanaro.customer.dto.request.TimeSlotAvailabilityRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.CallDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.TimeSlotAvailabilityResponse;
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
import com.hanaro.schedule_hanaro.global.websocket.handler.WebsocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
public class CallService {

	private final CallRepository callRepository;
	private final CustomerRepository customerRepository;
	private final WebsocketHandler websocketHandler;
	private final AdminCallService adminCallService;

	private static final int MAX_RESERVATION = 60;
	private static final int CONSULTANTS = 25;
	private static final int CONSULTATION_TIME_MINUTE = 15;

	@Transactional
	public CallResponse createCall(Authentication authentication, CallRequest request) throws InterruptedException {
		Customer customer = customerRepository.findById(PrincipalUtils.getId(authentication))
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CUSTOMER));

		LocalDateTime[] timeSlotRange = getTimeSlotRange(request.callDate());
		LocalDateTime startTime = timeSlotRange[0];
		LocalDateTime endTime = timeSlotRange[1];

		Category category = Category.fromCategoryName(request.category());

		if (callRepository.isExistReservationsInSlot(customer.getId(), startTime, endTime)) {
			throw new GlobalException(ErrorCode.CONFLICTING_CALL_RESERVATION, "같은 시간대에 이미 예약한 내역이 존재합니다.");
		}


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
					.category(category)
					.content(request.content())
					.tags("default")
					.build();

				Call savedCall = callRepository.save(newCall);

				AdminCallInfoResponse adminCallInfoResponse = adminCallService.getCallInfo(savedCall);
				websocketHandler.notifySubscribers(1L, "새로운 Call 등록: " + adminCallInfoResponse);

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

	@Transactional(readOnly = true)
	public List<TimeSlotAvailabilityResponse> getTimeSlotAvailability(TimeSlotAvailabilityRequest request) {
		LocalDate date = request.getDate();
		List<TimeSlotAvailabilityResponse> responses = new ArrayList<>();

		LocalDateTime startOfDay = date.atTime(9, 0);
		LocalDateTime endOfDay = date.atTime(18, 0);

		while (!startOfDay.isAfter(endOfDay)) {
			LocalDateTime[] timeSlotRange = getTimeSlotRange(startOfDay);
			LocalDateTime startTime = timeSlotRange[0];
			LocalDateTime endTime = timeSlotRange[1];

			int reservedCount = callRepository.countByCallDateBetween(startTime, endTime);
			int availableSlots = Math.max(0, MAX_RESERVATION - reservedCount);

			responses.add(TimeSlotAvailabilityResponse.builder()
				.timeSlot(startTime.toLocalTime() + "-" + endTime.plusSeconds(1).toLocalTime())
				.availableSlots(availableSlots)
				.build()
			);

			startOfDay = startOfDay.plusMinutes(30);
		}

		return responses;
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

		websocketHandler.notifySubscribers(1L, "Call 취소됨: " + callId);
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

		LocalDateTime[] timeSlotRange = getTimeSlotRange(call.getCallDate());
		String timeSlot = timeSlotRange[0].toLocalTime() + "-" +timeSlotRange[1].plusSeconds(1).toLocalTime();

		return CallDetailResponse.builder()
			.callId(call.getId())
			.customerName(call.getCustomer().getName())
			.callDate(call.getCallDate().toLocalDate().toString())
			.callTime(timeSlot)
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
