//package com.hanaro.schedule_hanaro.admin.service;
//
//import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInfoResponse;
//import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInquiryListResponse;
//import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerListResponse;
//import com.hanaro.schedule_hanaro.global.domain.Call;
//import com.hanaro.schedule_hanaro.global.domain.Customer;
//import com.hanaro.schedule_hanaro.global.domain.Inquiry;
//import com.hanaro.schedule_hanaro.global.domain.enums.Category;
//import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
//import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;
//import com.hanaro.schedule_hanaro.global.exception.GlobalException;
//import com.hanaro.schedule_hanaro.global.repository.CallRepository;
//import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
//import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.transaction.annotation.Transactional;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest
//@Transactional
//@Testcontainers
//class AdminCustomerServiceIntegrationTest {
//
//    @Container
//    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36")
//            .withDatabaseName("testdb")
//            .withUsername("test")
//            .withPassword("test");
//
//    @DynamicPropertySource
//    static void properties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", mysql::getJdbcUrl);
//        registry.add("spring.datasource.username", mysql::getUsername);
//        registry.add("spring.datasource.password", mysql::getPassword);
//        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
//    }
//
//    @Autowired
//    private AdminCustomerService adminCustomerService;
//
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    @Autowired
//    private CallRepository callRepository;
//
//    @Autowired
//    private InquiryRepository inquiryRepository;
//
//    private Customer testCustomer;
//
//    @BeforeEach
//    void setUp() {
//        // 테스트 데이터 초기화
//        customerRepository.deleteAll();
//        callRepository.deleteAll();
//        inquiryRepository.deleteAll();
//
//        // 테스트 고객 생성
//        testCustomer = Customer.builder()
//                .authId("test@test.com")
//                .password("password")
//                .name("테스트")
//                .phoneNum("01012345678")
//                .birth(LocalDate.of(1990, 1, 1))
//                .gender(Gender.MALE)
//                .build();
//
//        testCustomer = customerRepository.save(testCustomer);
//    }
//
//    @Test
//    @DisplayName("고객 정보 조회 통합 테스트")
//    void findCustomerInfoById() {
//        // when
//        AdminCustomerInfoResponse response = adminCustomerService.findCustomerInfoById(testCustomer.getId());
//
//        // then
//        assertThat(response.name()).isEqualTo(testCustomer.getName());
//        assertThat(response.authId()).isEqualTo(testCustomer.getAuthId());
//        assertThat(response.phone()).isEqualTo(testCustomer.getPhoneNum());
//    }
//
//    @Test
//    @DisplayName("고객 목록 조회 통합 테스트")
//    void getCustomerList() {
//        // when
//        AdminCustomerListResponse response = adminCustomerService.getCustomerList(1, 10);
//
//        // then
//        assertThat(response.customers()).isNotEmpty();
//        assertThat(response.currentPage()).isEqualTo(1);
//        assertThat(response.totalItems()).isGreaterThan(0);
//    }
//
//    @Test
//    @DisplayName("고객 문의 목록 조회 통합 테스트")
//    void findCustomerInquiryList() {
//        // given
//        // Call 생성
//        Call call = Call.builder()
//                .customer(testCustomer)
//                .callDate(LocalDateTime.now())
//                .callNum(1)
//                .category(Category.DEPOSIT)
//                .content("테스트 콜")
//                .startedAt(LocalDateTime.now())
//                .endedAt(LocalDateTime.now())
//                .tags("test")
//                .build();
//        callRepository.save(call);
//
//        // Inquiry 생성
//        Inquiry inquiry = Inquiry.builder()
//                .customer(testCustomer)
//                .content("테스트 문의")
//                .inquiryNum(1)
//                .category(Category.DEPOSIT)
//                .status(InquiryStatus.PENDING)
//                .tags("test")
//                .build();
//        inquiryRepository.save(inquiry);
//
//        // when
//        AdminCustomerInquiryListResponse response =
//            adminCustomerService.findCustomerInquiryList(testCustomer.getId());
//
//        // then
//        assertThat(response.phoneInquiries()).hasSize(1);
//        assertThat(response.oneToOneInquiries()).hasSize(1);
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 고객 조회 시 예외 발생 테스트")
//    void findCustomerInfoById_NotFound() {
//        // when & then
//        assertThrows(GlobalException.class, () ->
//            adminCustomerService.findCustomerInfoById(999L)
//        );
//    }
//
//    @Test
//    @DisplayName("잘못된 페이지 번호로 고객 목록 조회 시 예외 발생 테스트")
//    void getCustomerList_InvalidPage() {
//        // when & then
//        assertThrows(GlobalException.class, () ->
//            adminCustomerService.getCustomerList(0, 10)
//        );
//    }
//
//    @Test
//    @DisplayName("고객 문의 목록 조회 - 빈 목록 테스트")
//    void findCustomerInquiryList_EmptyLists() {
//        // given
//        Customer savedCustomer = customerRepository.save(testCustomer);
//
//        // when
//        AdminCustomerInquiryListResponse response =
//            adminCustomerService.findCustomerInquiryList(savedCustomer.getId());
//
//        // then
//        assertThat(response.phoneInquiries()).isEmpty();
//        assertThat(response.oneToOneInquiries()).isEmpty();
//    }
//
//    @Test
//    @DisplayName("고객 문의 목록 조회 - 다중 문의 테스트")
//    void findCustomerInquiryList_MultipleInquiries() {
//        // given
//        Customer savedCustomer = customerRepository.save(testCustomer);
//
//        // 여러 개의 Call 생성
//        List<Call> calls = List.of(
//            Call.builder()
//                .customer(savedCustomer)
//                .callDate(LocalDateTime.now())
//                .callNum(1)
//                .category(Category.DEPOSIT)
//                .content("첫 번째 콜")
//                .startedAt(LocalDateTime.now())
//                .endedAt(LocalDateTime.now())
//                .tags("test1")
//                .build(),
//            Call.builder()
//                .customer(savedCustomer)
//                .callDate(LocalDateTime.now())
//                .callNum(2)
//                .category(Category.LOAN)
//                .content("두 번째 콜")
//                .startedAt(LocalDateTime.now())
//                .endedAt(LocalDateTime.now())
//                .tags("test2")
//                .build()
//        );
//        callRepository.saveAll(calls);
//
//        // 여러 개의 Inquiry 생성
//        List<Inquiry> inquiries = List.of(
//            Inquiry.builder()
//                .customer(savedCustomer)
//                .content("첫 번째 문의")
//                .inquiryNum(1)
//                .category(Category.DEPOSIT)
//                .status(InquiryStatus.PENDING)
//                .tags("test1")
//                .build(),
//            Inquiry.builder()
//                .customer(savedCustomer)
//                .content("두 번째 문의")
//                .inquiryNum(2)
//                .category(Category.LOAN)
//                .status(InquiryStatus.REGISTRATIONCOMPLETE)
//                .tags("test2")
//                .build()
//        );
//        inquiryRepository.saveAll(inquiries);
//
//        // when
//        AdminCustomerInquiryListResponse response =
//            adminCustomerService.findCustomerInquiryList(savedCustomer.getId());
//
//        // then
//        assertThat(response.phoneInquiries()).hasSize(2);
//        assertThat(response.oneToOneInquiries()).hasSize(2);
//
//        // Call 검증
//        assertThat(response.phoneInquiries())
//            .extracting("content")
//            .containsExactlyInAnyOrder("첫 번째 콜", "두 번째 콜");
//
//        // Inquiry 검증
//        assertThat(response.oneToOneInquiries())
//            .extracting("content")
//            .containsExactlyInAnyOrder("첫 번째 문의", "두 번째 문의");
//    }
//
//    @Test
//    @DisplayName("고객 목록 페이징 테스트")
//    void getCustomerList_Paging() {
//        // given
//        List<Customer> customers = new ArrayList<>();
//        for (int i = 1; i <= 15; i++) {
//            customers.add(Customer.builder()
//                .authId("test" + i + "@test.com")
//                .password("password")
//                .name("테스트" + i)
//                .phoneNum("0101234" + String.format("%04d", i))
//                .birth(LocalDate.of(1990, 1, 1))
//                .gender(Gender.MALE)
//                .build());
//        }
//        customerRepository.saveAll(customers);
//
//        // when
//        AdminCustomerListResponse firstPage = adminCustomerService.getCustomerList(1, 10);
//        AdminCustomerListResponse secondPage = adminCustomerService.getCustomerList(2, 10);
//
//        // then
//        assertThat(firstPage.customers()).hasSize(10);
//        assertThat(secondPage.customers()).hasSize(6); // 15개 중 나머지
//        assertThat(firstPage.totalPages()).isEqualTo(2);
//        assertThat(firstPage.totalItems()).isEqualTo(16); // 기존 testCustomer 포함
//    }
//
//    @Test
//    @DisplayName("고객 정보 수정 권한 체크")
//    void checkCustomerEditPermission() {
//        // given
//        Customer savedCustomer = customerRepository.save(testCustomer);
//        Long invalidCustomerId = 999L;
//
//        // when & then
//        assertDoesNotThrow(() ->
//            adminCustomerService.findCustomerInfoById(savedCustomer.getId())
//        );
//
//        assertThrows(GlobalException.class, () ->
//            adminCustomerService.findCustomerInfoById(invalidCustomerId)
//        );
//    }
//}