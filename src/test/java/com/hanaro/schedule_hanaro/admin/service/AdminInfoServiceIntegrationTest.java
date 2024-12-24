// package com.hanaro.schedule_hanaro.admin.service;
//
// import com.hanaro.schedule_hanaro.admin.dto.response.AdminInfoResponse;
// import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
// import com.hanaro.schedule_hanaro.global.domain.Admin;
// import com.hanaro.schedule_hanaro.global.domain.Call;
// import com.hanaro.schedule_hanaro.global.domain.CallMemo;
// import com.hanaro.schedule_hanaro.global.domain.Customer;
// import com.hanaro.schedule_hanaro.global.domain.Inquiry;
// import com.hanaro.schedule_hanaro.global.domain.InquiryResponse;
// import com.hanaro.schedule_hanaro.global.domain.enums.Category;
// import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
// import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;
// import com.hanaro.schedule_hanaro.global.domain.enums.Role;
// import com.hanaro.schedule_hanaro.global.domain.enums.Status;
// import com.hanaro.schedule_hanaro.global.exception.GlobalException;
// import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
// import com.hanaro.schedule_hanaro.global.repository.CallRepository;
// import com.hanaro.schedule_hanaro.global.repository.CallMemoRepository;
// import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
// import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
// import com.hanaro.schedule_hanaro.global.repository.InquiryResponseRepository;
//
// import jakarta.persistence.EntityManager;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.security.core.Authentication;
// import org.springframework.test.context.DynamicPropertyRegistry;
// import org.springframework.test.context.DynamicPropertySource;
// import org.springframework.transaction.annotation.Transactional;
// import org.testcontainers.containers.MySQLContainer;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;
//
// import java.time.LocalDate;
// import java.time.LocalDateTime;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.BDDMockito.given;
// import static org.mockito.Mockito.mock;
//
// @SpringBootTest
// @Transactional
// @Testcontainers
// class AdminInfoServiceIntegrationTest {
//
//     @Container
//     static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36")
//             .withDatabaseName("testdb")
//             .withUsername("test")
//             .withPassword("test");
//
//     @DynamicPropertySource
//     static void properties(DynamicPropertyRegistry registry) {
//         registry.add("spring.datasource.url", mysql::getJdbcUrl);
//         registry.add("spring.datasource.username", mysql::getUsername);
//         registry.add("spring.datasource.password", mysql::getPassword);
//         registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
//     }
//
//     @Autowired
//     private AdminInfoService adminInfoService;
//
//     @Autowired
//     private AdminRepository adminRepository;
//
//     @Autowired
//     private CallRepository callRepository;
//
//     @Autowired
//     private InquiryRepository inquiryRepository;
//
//     @Autowired
//     private CustomerRepository customerRepository;
//
//     @Autowired
//     private CallMemoRepository callMemoRepository;
//
//     @Autowired
//     private InquiryResponseRepository inquiryResponseRepository;
//
//     @Autowired
//     private EntityManager entityManager;
//
//     private Admin testAdmin;
//     private Customer testCustomer;
//     private Authentication authentication;
//
//     @BeforeEach
//     void setUp() {
//         // 기존 데이터 정리
//         callRepository.deleteAll();
//         inquiryRepository.deleteAll();
//         customerRepository.deleteAll();
//         adminRepository.deleteAll();
//
//         // 테스트 관리자 생성
//         testAdmin = Admin.builder()
//                 .authId("testAdmin")
//                 .password("password")
//                 .name("테스트 관리자")
//                 .build();
//         testAdmin = adminRepository.save(testAdmin);
//
//         // 테스트 고객 생성
//         testCustomer = Customer.builder()
//                 .authId("test@test.com")
//                 .password("password")
//                 .name("테스트 고객")
//                 .phoneNum("01012345678")
//                 .birth(LocalDate.of(1990, 1, 1))
//                 .gender(Gender.MALE)
//                 .build();
//         testCustomer = customerRepository.save(testCustomer);
//
//         // Authentication 목업 설정
//         authentication = mock(Authentication.class);
//         CustomUserDetails userDetails = CustomUserDetails.of(
//             testAdmin.getId(),
//             testAdmin.getAuthId(),
//             testAdmin.getPassword(),
//             testAdmin.getRole()
//         );
//         given(authentication.getPrincipal()).willReturn(userDetails);
//
//         // 테스트 데이터 생성
//         createTestInquiries();
//     }
//
//     private void createTestInquiries() {
//         // Call 데이터 생성 및 Call_Memo 연결
//         for (int i = 0; i < 5; i++) {
//             Call call = Call.builder()
//                     .customer(testCustomer)
//                     .callDate(LocalDateTime.now())
//                     .callNum(i + 1)
//                     .category(Category.DEPOSIT)
//                     .content("테스트 콜 " + (i + 1))
//                     .startedAt(LocalDateTime.now())
//                     .endedAt(LocalDateTime.now())
//                     .tags("test")
//                     .build();
//             call.setStatus(Status.COMPLETE);
//             call = callRepository.save(call);
//
//             // Call_Memo 생성
//             CallMemo callMemo = CallMemo.builder()
//                     .call(call)
//                     .admin(testAdmin)
//                     .content("테스트 메모 " + (i + 1))
//                     .build();
//             callMemoRepository.save(callMemo);
//         }
//
//         // Inquiry 데이터 생성 및 Inquiry_Response 연결
//         for (int i = 0; i < 3; i++) {
//             Inquiry inquiry = Inquiry.builder()
//                     .customer(testCustomer)
//                     .content("테스트 문의 " + (i + 1))
//                     .inquiryNum(i + 1)
//                     .category(Category.DEPOSIT)
//                     .status(InquiryStatus.PENDING)
//                     .tags("test")
//                     .build();
//             inquiry = inquiryRepository.save(inquiry);
//
//             // Inquiry_Response 생성
//             InquiryResponse response = InquiryResponse.builder()
//                     .inquiry(inquiry)
//                     .admin(testAdmin)
//                     .content("테스트 답변 " + (i + 1))
//                     .createdAt(LocalDateTime.now())
//                     .build();
//             inquiryResponseRepository.save(response);
//         }
//     }
//
//     @Test
//     @DisplayName("관리자 통계 정상 조회 테스트")
//     void getAdminStats_Success() {
//         // when
//         AdminInfoResponse response = adminInfoService.getAdminStats(authentication);
//
//         // then
//         assertThat(response).isNotNull();
//         assertThat(response.adminId()).isEqualTo(testAdmin.getId());
//         assertThat(response.adminInfo().name()).isEqualTo(testAdmin.getName());
//
//         // 전화 상담 통계 검증
//         assertThat(response.phoneInquiryStats().total()).isEqualTo(5);
//
//         // 1:1 문의 통계 검증
//         assertThat(response.oneToOneInquiryStats().total()).isEqualTo(3);
//     }
//
//     @Test
//     @DisplayName("인증 정보 없이 조회 시 예외 발생 테스트")
//     void getAdminStats_WithoutAuthentication() {
//         // when & then
//         assertThrows(GlobalException.class, () ->
//             adminInfoService.getAdminStats(null)
//         );
//     }
//
//     @Test
//     @DisplayName("존재하지 않는 관리자로 조회 시 예외 발생 테스트")
//     void getAdminStats_WithNonExistentAdmin() {
//         // given
//         Authentication invalidAuth = mock(Authentication.class);
//         CustomUserDetails invalidUserDetails = CustomUserDetails.of(
//             999L,
//             "invalid",
//             "invalid",
//             Role.ADMIN
//         );
//         given(invalidAuth.getPrincipal()).willReturn(invalidUserDetails);
//
//         // when & then
//         assertThrows(GlobalException.class, () ->
//             adminInfoService.getAdminStats(invalidAuth)
//         );
//     }
//
//     @Test
//     @DisplayName("관리자의 통계 데이터가 없을 때 0으로 반환되는지 테스트")
//     void getAdminStats_NoData() {
//         // given
//         // 기존 데이터 삭제
//         callMemoRepository.deleteAll();
//         callRepository.deleteAll();
//         inquiryResponseRepository.deleteAll();
//         inquiryRepository.deleteAll();
//
//         // when
//         AdminInfoResponse response = adminInfoService.getAdminStats(authentication);
//
//         // then
//         assertThat(response).isNotNull();
//         assertThat(response.phoneInquiryStats().total()).isZero();
//         assertThat(response.oneToOneInquiryStats().total()).isZero();
//     }
//
//     @Test
//     @DisplayName("관리자의 오늘/주간/월간 통계가 정확히 계산되는지 테스트")
//     void getAdminStats_PeriodStats() {
//         // given
//         // 기존 데이터 삭제
//         callMemoRepository.deleteAll();
//         callRepository.deleteAll();
//         inquiryResponseRepository.deleteAll();
//         inquiryRepository.deleteAll();
//
//         LocalDateTime now = LocalDateTime.now();
//         LocalDateTime yesterday = now.minusDays(1);
//         LocalDateTime lastWeek = now.minusWeeks(1);
//         LocalDateTime lastMonth = now.minusMonths(1);
//
//         // 오늘 데이터
//         createTestData(now, 2);
//         // 어제 데이터
//         createTestData(yesterday, 3);
//         // 지난주 데이터
//         createTestData(lastWeek, 2);
//         // 지난달 데이터
//         createTestData(lastMonth, 3);
//
//         // when
//         AdminInfoResponse response = adminInfoService.getAdminStats(authentication);
//
//         // then
//         assertThat(response.phoneInquiryStats().today()).isEqualTo(2);
//         assertThat(response.phoneInquiryStats().weekly()).isEqualTo(7);  // 오늘(2) + 어제(3) + 지난주(2)
//         assertThat(response.phoneInquiryStats().monthly()).isEqualTo(10);  // 전체
//
//         assertThat(response.oneToOneInquiryStats().today()).isEqualTo(2);
//         assertThat(response.oneToOneInquiryStats().weekly()).isEqualTo(7);
//         assertThat(response.oneToOneInquiryStats().monthly()).isEqualTo(10);
//     }
//
//     private void createTestData(LocalDateTime dateTime, int count) {
//         for (int i = 0; i < count; i++) {
//             // Call 생성
//             Call call = Call.builder()
//                     .customer(testCustomer)
//                     .callDate(dateTime)
//                     .callNum(i + 1)
//                     .category(Category.DEPOSIT)
//                     .content("테스트 콜")
//                     .startedAt(dateTime)
//                     .endedAt(dateTime)
//                     .tags("test")
//                     .build();
//             call.setStatus(Status.COMPLETE);
//             call = callRepository.save(call);
//
//             CallMemo callMemo = CallMemo.builder()
//                     .call(call)
//                     .admin(testAdmin)
//                     .content("테스트 메모")
//                     .build();
//             callMemoRepository.save(callMemo);
//
//             // Inquiry 생성
//             Inquiry inquiry = Inquiry.builder()
//                     .customer(testCustomer)
//                     .content("테스트 문의")
//                     .inquiryNum(i + 1)
//                     .category(Category.DEPOSIT)
//                     .status(InquiryStatus.PENDING)
//                     .tags("test")
//                     .build();
//             inquiry = inquiryRepository.save(inquiry);
//
//             entityManager.createNativeQuery(
//                 "UPDATE Inquiry SET created_at = ? WHERE inquiry_id = ?")
//                 .setParameter(1, java.sql.Timestamp.valueOf(dateTime))
//                 .setParameter(2, inquiry.getId())
//                 .executeUpdate();
//
//             InquiryResponse response = InquiryResponse.builder()
//                     .inquiry(inquiry)
//                     .admin(testAdmin)
//                     .content("테스트 답변")
//                     .createdAt(dateTime)
//                     .build();
//             inquiryResponseRepository.save(response);
//         }
//
//         // 변경사항을 즉시 반영
//         entityManager.flush();
//         entityManager.clear();
//     }
// }