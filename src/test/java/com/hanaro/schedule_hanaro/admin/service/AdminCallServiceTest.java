package com.hanaro.schedule_hanaro.admin.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import com.hanaro.schedule_hanaro.customer.service.CallService;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import com.hanaro.schedule_hanaro.global.repository.BranchRepository;
import com.hanaro.schedule_hanaro.global.repository.CallRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminCallServiceTest {

	@Autowired
	private AdminCallService AdmincallService;

	@Autowired
	private CallService callService;
	@Autowired
	private CallRepository callRepository;
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private EntityManager entityManager;


	private Admin admin;
	@Autowired
	private BranchRepository branchRepository;
	@Autowired
	private CustomerRepository customerRepository;

	@BeforeAll
	public void beforeAll() {
		// Call 생성
		for (int i = 10; i <= 20; i++) {
			Customer customer = Customer
				.builder()
				.authId("TestAuthId" + i)
				.name("TestUser" + i)
				.password("TestPassword")
				.phoneNum("01012341234")
				.birth(LocalDate.of(2002, 4, 15))
				.gender(Gender.FEMALE)
				.build();
			customerRepository.save(customer);

			Call call = Call.builder()
				.callNum(i)
				.customer(customer)
				.callDate(LocalDateTime.now())
				.tags("tags" + i)
				.content("content" + i)
				.category(Category.LOAN)
				.build();
			callRepository.save(call);
		}
	}

	@AfterAll
	public void afterAll() {
		for (int i = 10; i <= 20; i++) {
			Call call = callRepository.findByCallNum(i);
			callRepository.delete(call);

			Customer customer = customerRepository.findByName("TestUser" + i).orElseThrow();
			customerRepository.delete(customer);
		}
	}

	@Test
	public void testPessimisticLockInCallProgress() throws InterruptedException {
		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(admin);

		// 동시 실행을 위한 쓰레드 설정
		int threadCount = 5;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		List<Future<Long>> results = new ArrayList<>();

		for (int i = 0; i < threadCount; i++) {
			results.add(executorService.submit(() -> {
				try {
					return AdmincallService.changeCallStatusProgress(authentication);
				} catch (Exception e) {
					return null;
				} finally {
					latch.countDown();
				}
			}));
		}

		latch.await(); // 모든 스레드가 종료될 때까지 대기
		executorService.shutdown();

		// When
		List<Call> progressedCalls = callRepository.findByStatus(Status.PROGRESS);

		// Then
		assertEquals(1, progressedCalls.size(), "동시에 접근해도 하나의 상담만 할당되어야 합니다.");
	}
}
