// package com.hanaro.schedule_hanaro.admin.controller;
//
// import com.hanaro.schedule_hanaro.global.auth.provider.JwtTokenProvider;
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
// import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
// import com.hanaro.schedule_hanaro.global.repository.CallMemoRepository;
// import com.hanaro.schedule_hanaro.global.repository.CallRepository;
// import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
// import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
// import com.hanaro.schedule_hanaro.global.repository.InquiryResponseRepository;
// import jakarta.transaction.Transactional;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.test.context.DynamicPropertyRegistry;
// import org.springframework.test.context.DynamicPropertySource;
// import org.springframework.test.util.ReflectionTestUtils;
// import org.springframework.test.web.servlet.MockMvc;
// import org.testcontainers.containers.MySQLContainer;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;
//
// import java.time.LocalDateTime;
// import java.time.LocalDate;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional
// @Testcontainers
// class AdminInfoControllerIntegrationTest {
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
//     private MockMvc mockMvc;
//
//     @Autowired
//     private AdminRepository adminRepository;
//
//     @Autowired
//     private JwtTokenProvider jwtTokenProvider;
//
//     @Autowired
//     private CallRepository callRepository;
//
//     @Autowired
//     private InquiryRepository inquiryRepository;
//
//     @Autowired
//     private CallMemoRepository callMemoRepository;
//
//     @Autowired
//     private InquiryResponseRepository inquiryResponseRepository;
//
//     @Autowired
//     private CustomerRepository customerRepository;
//
//     private Admin testAdmin;
//     private String adminToken;
//     private int customerCounter = 0;
//
//     @BeforeEach
//     void setUp() {
//         // 기존 데이터 정리
//         adminRepository.deleteAll();
//
//         // 테스트 관리자 생성
//         testAdmin = Admin.builder()
//                 .authId("testAdmin")
//                 .password(new BCryptPasswordEncoder().encode("password"))
//                 .name("테스트 관리자")
//                 .build();
//         ReflectionTestUtils.setField(testAdmin, "role", Role.ADMIN);
//         testAdmin = adminRepository.save(testAdmin);
//
//         // 관리자 토큰 생성
//         adminToken = "Bearer " + jwtTokenProvider.generateToken(testAdmin.getAuthId(), Role.ADMIN, 1);
//     }
//
//     @Test
//     @DisplayName("관리자 통계 정보 정상 조회")
//     void getAdminStats_Success() throws Exception {
//         mockMvc.perform(get("/admin/api/admins/stats")
//                 .header("Authorization", adminToken))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.adminId").exists())
//                 .andExpect(jsonPath("$.adminInfo").exists())
//                 .andExpect(jsonPath("$.adminInfo.name").value("테스트 관리자"))
//                 .andExpect(jsonPath("$.phoneInquiryStats").exists())
//                 .andExpect(jsonPath("$.phoneInquiryStats.today").exists())
//                 .andExpect(jsonPath("$.phoneInquiryStats.weekly").exists())
//                 .andExpect(jsonPath("$.phoneInquiryStats.monthly").exists())
//                 .andExpect(jsonPath("$.phoneInquiryStats.total").exists())
//                 .andExpect(jsonPath("$.oneToOneInquiryStats").exists())
//                 .andExpect(jsonPath("$.oneToOneInquiryStats.today").exists())
//                 .andExpect(jsonPath("$.oneToOneInquiryStats.weekly").exists())
//                 .andExpect(jsonPath("$.oneToOneInquiryStats.monthly").exists())
//                 .andExpect(jsonPath("$.oneToOneInquiryStats.total").exists());
//     }
//
//
//     @Test
//     @DisplayName("Authorization 헤더 없이 요청 시 403 응답")
//     void getAdminStats_NoAuthHeader() throws Exception {
//         mockMvc.perform(get("/admin/api/admins/stats"))
//                 .andExpect(status().isForbidden());
//     }
//
//     @Test
//     @DisplayName("잘못된 Authorization 헤더 형식으로 요청 시 403 응답")
//     void getAdminStats_InvalidAuthHeaderFormat() throws Exception {
//         mockMvc.perform(get("/admin/api/admins/stats")
//                 .header("Authorization", "Bearer")) // Bearer 뒤에 토큰이 없는 경우
//                 .andExpect(status().isForbidden());
//     }
//
//     @Test
//     @DisplayName("관리자 통계 정보와 데이터베이스 상태 일치 검증")
//     @SuppressWarnings("unused")
//     void getAdminStats_DataIntegrity() throws Exception {
//         // given
//         // 전화 상담 데이터 생성
//         Call todayCall = createCall(LocalDateTime.now());
//         Call weeklyCall = createCall(LocalDateTime.now().minusDays(3));
//         Call monthlyCall = createCall(LocalDateTime.now().minusDays(20));
//         Call oldCall = createCall(LocalDateTime.now().minusDays(40));
//
//         // 1:1 문의 데이터 생성
//         Inquiry todayInquiry = createInquiry(LocalDateTime.now());
//         Inquiry weeklyInquiry = createInquiry(LocalDateTime.now().minusDays(5));
//         Inquiry monthlyInquiry = createInquiry(LocalDateTime.now().minusDays(25));
//         Inquiry oldInquiry = createInquiry(LocalDateTime.now().minusDays(45));
//
//         // when & then
//         mockMvc.perform(get("/admin/api/admins/stats")
//                 .header("Authorization", adminToken))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.adminId").value(testAdmin.getId()))
//                 .andExpect(jsonPath("$.adminInfo.name").value(testAdmin.getName()))
//                 // 전화 상담 통계 검증
//                 .andExpect(jsonPath("$.phoneInquiryStats.today").value(1))
//                 .andExpect(jsonPath("$.phoneInquiryStats.weekly").value(2))
//                 .andExpect(jsonPath("$.phoneInquiryStats.monthly").value(3))
//                 .andExpect(jsonPath("$.phoneInquiryStats.total").value(4))
//                 // 1:1 문의 통계 검증
//                 .andExpect(jsonPath("$.oneToOneInquiryStats.today").value(1))
//                 .andExpect(jsonPath("$.oneToOneInquiryStats.weekly").value(2))
//                 .andExpect(jsonPath("$.oneToOneInquiryStats.monthly").value(3))
//                 .andExpect(jsonPath("$.oneToOneInquiryStats.total").value(4));
//     }
//
//     @Test
//     @DisplayName("관리자 통계 정보 - 빈 데이터 검증")
//     void getAdminStats_EmptyData() throws Exception {
//         // given
//         callRepository.deleteAll();
//         inquiryRepository.deleteAll();
//
//         // when & then
//         mockMvc.perform(get("/admin/api/admins/stats")
//                 .header("Authorization", adminToken))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.adminId").value(testAdmin.getId()))
//                 .andExpect(jsonPath("$.adminInfo.name").value(testAdmin.getName()))
//                 // 전화 상담 통계 검증
//                 .andExpect(jsonPath("$.phoneInquiryStats.today").value(0))
//                 .andExpect(jsonPath("$.phoneInquiryStats.weekly").value(0))
//                 .andExpect(jsonPath("$.phoneInquiryStats.monthly").value(0))
//                 .andExpect(jsonPath("$.phoneInquiryStats.total").value(0))
//                 // 1:1 문의 통계 검증
//                 .andExpect(jsonPath("$.oneToOneInquiryStats.today").value(0))
//                 .andExpect(jsonPath("$.oneToOneInquiryStats.weekly").value(0))
//                 .andExpect(jsonPath("$.oneToOneInquiryStats.monthly").value(0))
//                 .andExpect(jsonPath("$.oneToOneInquiryStats.total").value(0));
//     }
//
//     // 테스트 데이터 생성을 위한 헬퍼 메서드
//     private Call createCall(LocalDateTime dateTime) {
//         Call call = Call.builder()
//                 .callDate(dateTime)
//                 .customer(createCustomer())
//                 .callNum(1)
//                 .category(Category.DEPOSIT)
//                 .content("테스트 내용")
//                 .startedAt(dateTime)
//                 .endedAt(dateTime.plusMinutes(Category.DEPOSIT.getWaitTime()))
//                 .tags("테스트")
//                 .build();
//         call.setStatus(Status.COMPLETE);
//         call = callRepository.save(call);
//
//         CallMemo memo = CallMemo.builder()
//                 .call(call)
//                 .admin(testAdmin)
//                 .content("테스트 메모")
//                 .build();
//         callMemoRepository.save(memo);
//
//         return call;
//     }
//
//     private Customer createCustomer() {
//         customerCounter++;
//         Customer customer = Customer.builder()
//                 .authId("testCustomer" + customerCounter)
//                 .password("password123")
//                 .name("테스트 고객" + customerCounter)
//                 .phoneNum("0101234" + String.format("%04d", customerCounter))
//                 .birth(LocalDate.of(1990, 1, 1))
//                 .gender(Gender.MALE)
//                 .build();
//         return customerRepository.save(customer);
//     }
//
//     private Inquiry createInquiry(LocalDateTime dateTime) {
//         Inquiry inquiry = Inquiry.builder()
//                 .customer(createCustomer())
//                 .content("테스트 내용")
//                 .inquiryNum(1)
//                 .category(Category.DEPOSIT)
//                 .status(InquiryStatus.REGISTRATIONCOMPLETE)
//                 .tags("테스트")
//                 .build();
//         inquiry = inquiryRepository.save(inquiry);
//
//         InquiryResponse response = InquiryResponse.builder()
//                 .inquiry(inquiry)
//                 .admin(testAdmin)
//                 .content("테스트 답변")
//                 .build();
//         inquiryResponseRepository.save(response);
//
//         return inquiry;
//     }
//
// }