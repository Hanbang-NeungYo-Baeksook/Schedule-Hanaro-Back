// package com.hanaro.schedule_hanaro.customer.service;
//
// import static org.assertj.core.api.Assertions.*;
//
// import java.time.LocalDate;
// import java.util.List;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
//
// import com.hanaro.schedule_hanaro.global.domain.enums.Category;
// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
//
// import com.hanaro.schedule_hanaro.customer.dto.request.VisitCreateRequest;
// import com.hanaro.schedule_hanaro.global.repository.BranchRepository;
// import com.hanaro.schedule_hanaro.global.repository.CsVisitRepository;
// import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
// import com.hanaro.schedule_hanaro.global.repository.VisitRepository;
// import com.hanaro.schedule_hanaro.global.domain.Branch;
// import com.hanaro.schedule_hanaro.global.domain.CsVisit;
// import com.hanaro.schedule_hanaro.global.domain.Customer;
// import com.hanaro.schedule_hanaro.global.domain.Visit;
// import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;
// import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
//
// @SpringBootTest
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// public class VisitServiceTest {
// 	@Autowired
// 	VisitService visitService;
//
// 	@Autowired
// 	CustomerRepository customerRepository;
//
// 	@Autowired
// 	BranchRepository branchRepository;
//
// 	@Autowired
// 	VisitRepository visitRepository;
//
// 	@Autowired
// 	CsVisitRepository csVisitRepository;
//
// 	@BeforeAll
// 	public void beforeAll() {
// 		for (int i = 1; i <= 100; i++) {
// 			Customer customer = Customer
// 				.builder()
// 				.authId("TestAuthId" + i)
// 				.name("TestUser" + i)
// 				.password("TestPassword")
// 				.phoneNum("01012341234")
// 				.birth(LocalDate.of(2002, 4, 15))
// 				.gender(Gender.FEMALE)
// 				.build();
// 			customerRepository.save(customer);
// 		}
//
// 		Branch branch = Branch
// 			.builder()
// 			.address("TestAddress")
// 			.name("NormalTestBranch")
// 			.businessTime("00:00~23:59")
// 			.tel("021231234")
// 			.xPosition("12.1234")
// 			.yPosition("21.1234")
// 			.branchType(BranchType.BANK)
// 			.build();
// 		branchRepository.save(branch);
//
// 		CsVisit csVisit = CsVisit
// 			.builder()
// 			.date(LocalDate.now())
// 			// .totalNum(0)
// 			// .waitAmount(0)
// 			.branch(branch)
// 			.build();
// 		csVisitRepository.save(csVisit);
// 	}
//
// 	@AfterAll
// 	public void afterAll() {
// 		Branch branch = branchRepository.findByName("NormalTestBranch").orElseThrow();
// 		CsVisit csVisit = csVisitRepository.findByBranchId(branch.getId()).orElseThrow();
// 		List<Visit> visits = visitRepository.findAllBySection_Id(branch.getId());
// 		csVisitRepository.delete(csVisit);
// 		visitRepository.deleteAll(visits);
//
// 		for (int i = 1; i <= 100; i++) {
// 			Customer customer = customerRepository.findByName("TestUser" + i).orElseThrow();
// 			customerRepository.delete(customer);
// 		}
// 	}
//
// 	// @Test
// 	// public void addVisitRequest100Test() throws Exception {
// 	// 	int threadCount = 2;
// 	// 	ExecutorService executorService = Executors.newFixedThreadPool(2);
// 	// 	CountDownLatch latch = new CountDownLatch(threadCount);
// 	//
// 	// 	Branch branch = branchRepository.findByName("NormalTestBranch").orElseThrow();
// 	// 	for (int i = 0; i < threadCount; i++) {
// 	// 		int finalI = i;
// 	// 		executorService.submit(() -> {
// 	// 				try {
// 	// 					Customer customer = customerRepository.findByName("TestUser" + (finalI + 1)).orElseThrow();
// 	// 					VisitCreateRequest visitCreateRequest = VisitCreateRequest
// 	// 						.builder()
// 	// 						.customerId(customer.getId())
// 	// 						.branchId(branch.getId())
// 	// 						.content("TestContent")
// 	// 						.build();
// 	// 					visitService.addVisitReservation(visitCreateRequest);
// 	// 				} catch (InterruptedException e) {
// 	// 					System.err.println("Error: " + e.getMessage());
// 	// 				} finally {
// 	// 					latch.countDown();
// 	// 				}
// 	// 			}
// 	// 		);
// 	// 	}
// 	//
// 	// 	latch.await();
// 	// 	executorService.shutdown();
// 	//
// 	// 	CsVisit csVisit = csVisitRepository.findByBranchIdAndDate(branch.getId(), LocalDate.now()).orElseThrow();
// 	//
// 	// 	assertThat(csVisit.getTotalNum()).isEqualTo(100);
// 	// }
//
// 	@Test
// 	public void deleteVisitReservationTest() throws InterruptedException {
// 		Branch branch = branchRepository.findByName("NormalTestBranch").orElseThrow();
// 		Customer customer = customerRepository.findByName("TestUser5").orElseThrow();
// 		Authentication authentication = new UsernamePasswordAuthenticationToken(customer.getId(), null);
//
// 		Long visitId = visitService.addVisitReservation(VisitCreateRequest.builder()
// 				.branchId(branch.getId())
// 				.content("Test Content")
// 				.category(Category.SIGNIN)
// 				.build(),authentication).visitId();
//
// 		// Delete visit reservation
// 		visitService.deleteVisitReservation(visitId);
//
// 		// Verify deletion
// 		Visit visit = visitRepository.findById(visitId).orElse(null);
// 		assertThat(visit).isNull();
// 	}
//
// 	@Test
// 	public void addVisitRequest100Test() throws Exception {
// 		int threadCount = 100;
// 		ExecutorService executorService = Executors.newFixedThreadPool(10);
// 		CountDownLatch latch = new CountDownLatch(threadCount);
//
// 		Branch branch = branchRepository.findByName("NormalTestBranch").orElseThrow();
//
// 		for (int i = 0; i < threadCount; i++) {
// 			int finalI = i;
// 			executorService.submit(() -> {
// 				try {
// 					Customer customer = customerRepository.findByName("TestUser" + (finalI + 1)).orElseThrow();
// 					Authentication authentication = new UsernamePasswordAuthenticationToken(customer.getId(), null);
// 					VisitCreateRequest visitCreateRequest = VisitCreateRequest.builder()
// 							.branchId(branch.getId())
// 							.content("TestContent")
// 							.category(Category.SIGNIN)
// 							.build();
// 					visitService.addVisitReservation(visitCreateRequest,authentication);
// 				} catch (Exception e) {
// 					e.printStackTrace();
// 				} finally {
// 					latch.countDown();
// 				}
// 			});
// 		}
//
// 		latch.await();
// 		executorService.shutdown();
//
// 		CsVisit csVisit = csVisitRepository.findByBranchIdAndDate(branch.getId(), LocalDate.now()).orElseThrow();
// 		assertThat(csVisit.getTotalNum()).isEqualTo(threadCount);
// 	}
//
// }
// // package com.hanaro.schedule_hanaro.customer.service;
// //
// // import static org.assertj.core.api.Assertions.*;
// //
// // import java.time.LocalDate;
// // import java.util.List;
// // import java.util.concurrent.CountDownLatch;
// // import java.util.concurrent.ExecutorService;
// // import java.util.concurrent.Executors;
// //
// // import org.junit.jupiter.api.AfterAll;
// // import org.junit.jupiter.api.BeforeAll;
// // import org.junit.jupiter.api.Test;
// // import org.junit.jupiter.api.TestInstance;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.boot.test.context.SpringBootTest;
// //
// // import com.hanaro.schedule_hanaro.customer.dto.request.VisitCreateRequest;
// // import com.hanaro.schedule_hanaro.global.repository.BranchRepository;
// // import com.hanaro.schedule_hanaro.global.repository.CsVisitRepository;
// // import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
// // import com.hanaro.schedule_hanaro.global.repository.VisitRepository;
// // import com.hanaro.schedule_hanaro.global.domain.Branch;
// // import com.hanaro.schedule_hanaro.global.domain.CsVisit;
// // import com.hanaro.schedule_hanaro.global.domain.Customer;
// // import com.hanaro.schedule_hanaro.global.domain.Visit;
// // import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;
// // import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
// //
// // @SpringBootTest
// // @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// // public class VisitServiceTest {
// // 	@Autowired
// // 	VisitService visitService;
// //
// // 	@Autowired
// // 	CustomerRepository customerRepository;
// //
// // 	@Autowired
// // 	BranchRepository branchRepository;
// //
// // 	@Autowired
// // 	VisitRepository visitRepository;
// //
// // 	@Autowired
// // 	CsVisitRepository csVisitRepository;
// //
// // 	@BeforeAll
// // 	public void beforeAll() {
// // 		for (int i = 1; i <= 100; i++) {
// // 			Customer customer = Customer
// // 				.builder()
// // 				.authId("TestAuthId" + i)
// // 				.name("TestUser" + i)
// // 				.password("TestPassword")
// // 				.phoneNum("01012341234")
// // 				.birth(LocalDate.of(2002, 4, 15))
// // 				.gender(Gender.FEMALE)
// // 				.build();
// // 			customerRepository.save(customer);
// // 		}
// //
// // 		Branch branch = Branch
// // 			.builder()
// // 			.address("TestAddress")
// // 			.name("NormalTestBranch")
// // 			.businessTime("00:00~23:59")
// // 			.tel("021231234")
// // 			.xPosition("12.1234")
// // 			.yPosition("21.1234")
// // 			.branchType(BranchType.BANK)
// // 			.build();
// // 		branchRepository.save(branch);
// //
// // 		CsVisit csVisit = CsVisit
// // 			.builder()
// // 			.date(LocalDate.now())
// // 			// .totalNum(0)
// // 			// .waitAmount(0)
// // 			.branch(branch)
// // 			.build();
// // 		csVisitRepository.save(csVisit);
// // 	}
// //
// // 	@AfterAll
// // 	public void afterAll() {
// // 		Branch branch = branchRepository.findByName("NormalTestBranch").orElseThrow();
// // 		CsVisit csVisit = csVisitRepository.findByBranchId(branch.getId()).orElseThrow();
// // 		List<Visit> visits = visitRepository.findAllBySection_Id(branch.getId());
// // 		csVisitRepository.delete(csVisit);
// // 		visitRepository.deleteAll(visits);
// //
// // 		for (int i = 1; i <= 100; i++) {
// // 			Customer customer = customerRepository.findByName("TestUser" + i).orElseThrow();
// // 			customerRepository.delete(customer);
// // 		}
// // 	}
// //
// // 	// @Test
// // 	// public void addVisitRequest100Test() throws Exception {
// // 	// 	int threadCount = 2;
// // 	// 	ExecutorService executorService = Executors.newFixedThreadPool(2);
// // 	// 	CountDownLatch latch = new CountDownLatch(threadCount);
// // 	//
// // 	// 	Branch branch = branchRepository.findByName("NormalTestBranch").orElseThrow();
// // 	// 	for (int i = 0; i < threadCount; i++) {
// // 	// 		int finalI = i;
// // 	// 		executorService.submit(() -> {
// // 	// 				try {
// // 	// 					Customer customer = customerRepository.findByName("TestUser" + (finalI + 1)).orElseThrow();
// // 	// 					VisitCreateRequest visitCreateRequest = VisitCreateRequest
// // 	// 						.builder()
// // 	// 						.customerId(customer.getId())
// // 	// 						.branchId(branch.getId())
// // 	// 						.content("TestContent")
// // 	// 						.build();
// // 	// 					visitService.addVisitReservation(visitCreateRequest);
// // 	// 				} catch (InterruptedException e) {
// // 	// 					System.err.println("Error: " + e.getMessage());
// // 	// 				} finally {
// // 	// 					latch.countDown();
// // 	// 				}
// // 	// 			}
// // 	// 		);
// // 	// 	}
// // 	//
// // 	// 	latch.await();
// // 	// 	executorService.shutdown();
// // 	//
// // 	// 	CsVisit csVisit = csVisitRepository.findByBranchIdAndDate(branch.getId(), LocalDate.now()).orElseThrow();
// // 	//
// // 	// 	assertThat(csVisit.getTotalNum()).isEqualTo(100);
// // 	// }
// // }
