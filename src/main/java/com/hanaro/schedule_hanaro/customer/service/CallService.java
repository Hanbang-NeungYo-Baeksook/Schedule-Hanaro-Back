package com.hanaro.schedule_hanaro.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.customer.dto.request.CallRequest;
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

}
