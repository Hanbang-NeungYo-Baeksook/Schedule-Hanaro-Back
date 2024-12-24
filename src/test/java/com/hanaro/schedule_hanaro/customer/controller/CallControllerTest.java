// package com.hanaro.schedule_hanaro.customer.controller;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.nio.charset.StandardCharsets;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.Comparator;
// import java.util.Date;
// import java.util.List;
// import java.util.Optional;
// import java.util.Set;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.stream.Collectors;
//
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.context.ActiveProfiles;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.hanaro.schedule_hanaro.customer.TestSecurityConfig;
// import com.hanaro.schedule_hanaro.customer.service.CallService;
// import com.hanaro.schedule_hanaro.global.domain.Call;
// import com.hanaro.schedule_hanaro.global.domain.Customer;
// import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
// import com.hanaro.schedule_hanaro.global.repository.CallMemoRepository;
// import com.hanaro.schedule_hanaro.global.repository.CallRepository;
// import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
// import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
//
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.security.Keys;
//
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// @Import(TestSecurityConfig.class)
// public class CallControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private CallRepository callRepository;
//
// 	@BeforeEach
// 	void setUp() {
// 		callRepository.deleteAll();
// 	}
//
// 	@Test
// 	public void testConcurrentCreateCallWithoutAuth() throws Exception {
// 		LocalDateTime callDate = LocalDateTime.of(2024, 12, 25, 10, 30);
// 		String category = "DEPOSIT";
// 		String content = "Test";
//
// 		int threadCount = 100;
// 		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
// 		CountDownLatch latch = new CountDownLatch(threadCount);
//
// 		for (int i = 0; i < threadCount; i++) {
// 			executor.execute(() -> {
// 				try {
// 					Thread.sleep((long) (Math.random() * 30000));
// 					mockMvc.perform(post("/api/calls")
// 							.contentType(MediaType.APPLICATION_JSON)
// 							.content(String.format("""
//                         {
//                             "call_date": "%s",
//                             "category": "%s",
//                             "content": "%s"
//                         }
//                         """, callDate, category, content)))
// 						.andExpect(status().isCreated());
// 				} catch (Exception e) {
// 					e.printStackTrace();
// 				} finally {
// 					latch.countDown();
// 				}
// 			});
// 		}
//
//
// 		latch.await();
// 		executor.shutdown();
//
// 		List<Call> calls = callRepository.findAll();
// 		assertEquals(threadCount, calls.size(), "총 예약 개수가 일치하지 않음");
//
// 		Set<Integer> uniqueCallNums = calls.stream()
// 			.map(Call::getCallNum)
// 			.collect(Collectors.toSet());
// 		assertEquals(threadCount, uniqueCallNums.size(), "모든 번호표는 고유해야 함");
// 	}
// }
