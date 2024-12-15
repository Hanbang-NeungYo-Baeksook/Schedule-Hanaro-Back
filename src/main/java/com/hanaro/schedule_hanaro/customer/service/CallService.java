package com.hanaro.schedule_hanaro.customer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.customer.dto.request.CallRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.CallListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.CallResponse;
import com.hanaro.schedule_hanaro.customer.repository.CallRepository;
import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

@Service
public class CallService {

	@Autowired
	private CallRepository callRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Transactional
	public CallResponse createCall(Long customerId, CallRequest request) {
		Customer customer = customerRepository.findById(customerId)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원 id입니다."));

		boolean isDuplicate = callRepository.existsByCallDate(request.callDate());

		if (isDuplicate) {
			throw new IllegalStateException("이미 예약된 시간대입니다.");
		}

		int newCallNum = callRepository.findMaxCallNumByDate(request.callDate()) + 1;

		Call call = Call.builder()
			.customer(customer)
			.callDate(request.callDate())
			.callNum(newCallNum)
			.category(Category.valueOf(request.category().toUpperCase()))
			.status(Status.PENDING)
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
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상담 id입니다."));

		if (call.getStatus() != Status.PENDING) {
			throw new IllegalStateException("진행 중이거나 완료된 상담은 취소할 수 없습니다.");
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

}
