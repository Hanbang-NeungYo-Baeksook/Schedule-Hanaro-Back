package com.hanaro.schedule_hanaro.customer.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallInfoResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminCallService;
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
import com.hanaro.schedule_hanaro.global.websocket.handler.WebsocketHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallService {

	private final CallRepository callRepository;
	private final CustomerRepository customerRepository;
	private final WebsocketHandler websocketHandler;
	private final AdminCallService adminCallService;

	@Transactional
	public CallResponse createCall(Authentication authentication, CallRequest request) {

		Customer customer = customerRepository.findById(PrincipalUtils.getId(authentication))
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원 id입니다."));

		boolean isDuplicate = callRepository.existsByCallDate(request.callDate());

		if (isDuplicate) {
			throw new IllegalStateException("이미 예약된 시간대입니다.");
		}

		LocalDate requestDate = request.callDate().toLocalDate();

		int newCallNum = callRepository.findMaxCallNumByDate(requestDate) + 1;

		Call call = Call.builder()
			.customer(customer)
			.callDate(request.callDate())
			.callNum(newCallNum)
			.category(Category.valueOf(request.category().toUpperCase()))
			.content(request.content())
			.tags("default")
			.build();

		Call savedCall = callRepository.save(call);

		AdminCallInfoResponse adminCallInfoResponse = adminCallService.getCallInfo(savedCall);
		websocketHandler.notifySubscribers(1L, "새로운 Call 등록: " + adminCallInfoResponse);

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

		callRepository.delete(call);
	}

	public CallListResponse getCallList(String status, int page, int size) {
		Status callStatus = Status.valueOf(status.toUpperCase());

		Pageable pageable = PageRequest.of(page - 1, size);

		Slice<Call> callSlice = callRepository.findByStatus(callStatus, pageable);

		List<CallListResponse.CallData> callDataList = callSlice.getContent().stream()
			.map(call -> CallListResponse.CallData.builder()
				.callId(call.getId())
				.callDate(call.getCallDate().toLocalDate().toString())
				.callTime(call.getCallDate().toLocalTime().toString())
				.callNum(call.getCallNum())
				.category(call.getCategory().name())
				.status(call.getStatus().getStatus())
				.build())
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

		return CallDetailResponse.builder()
			.callId(call.getId())
			.customerName(call.getCustomer().getName())
			.callDate(call.getCallDate().toLocalDate().toString())
			.callTime(call.getCallDate().toLocalTime().toString() + "Z")
			.callNum(call.getCallNum())
			.category(call.getCategory().name())
			.status(call.getStatus().getStatus())
			.content(call.getContent())
			//이 부분 tag 어떤식으로 넘어오는지 모르겠어서 일단 이렇게 구현했습니다..
			.tags(List.of(call.getTags().split(",")))
			.build();
	}

}
