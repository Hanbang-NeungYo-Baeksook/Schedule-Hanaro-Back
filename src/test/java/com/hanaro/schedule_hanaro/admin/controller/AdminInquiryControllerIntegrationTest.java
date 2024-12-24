// package com.hanaro.schedule_hanaro.admin.controller;
//
// import com.hanaro.schedule_hanaro.admin.dto.request.AdminInquiryResponseRequest;
// import com.hanaro.schedule_hanaro.global.auth.provider.JwtTokenProvider;
// import com.hanaro.schedule_hanaro.global.domain.Admin;
// import com.hanaro.schedule_hanaro.global.domain.Customer;
// import com.hanaro.schedule_hanaro.global.domain.Inquiry;
// import com.hanaro.schedule_hanaro.global.domain.enums.Category;
// import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
// import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;
// import com.hanaro.schedule_hanaro.global.domain.enums.Role;
// import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
// import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
// import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
// import jakarta.transaction.Transactional;
// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.testcontainers.containers.MySQLContainer;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.test.web.servlet.MockMvc;
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.test.util.ReflectionTestUtils;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.DynamicPropertyRegistry;
// import org.springframework.test.context.DynamicPropertySource;
// import java.time.LocalDate;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional
// @Testcontainers
// class AdminInquiryControllerIntegrationTest {
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
//     private ObjectMapper objectMapper;
//
//     @Autowired
//     private AdminRepository adminRepository;
//
//     @Autowired
//     private CustomerRepository customerRepository;
//
//     @Autowired
//     private InquiryRepository inquiryRepository;
//
//     @Autowired
//     private JwtTokenProvider jwtTokenProvider;
//
//     private Admin testAdmin;
//     private Customer testCustomer;
//     private Inquiry testInquiry;
//     private String adminToken;
//
//     @BeforeEach
//     void setUp() {
//         // 기존 데이터 정리
//         inquiryRepository.deleteAll();
//         customerRepository.deleteAll();
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
//         // 테스트 문의 생성
//         testInquiry = Inquiry.builder()
//                 .customer(testCustomer)
//                 .content("테스트 문의 내용")
//                 .inquiryNum(1)
//                 .category(Category.DEPOSIT)
//                 .status(InquiryStatus.PENDING)
//                 .tags("test")
//                 .build();
//         testInquiry = inquiryRepository.save(testInquiry);
//
//         // 관리자 토큰 생성
//         adminToken = "Bearer " + jwtTokenProvider.generateToken(testAdmin.getAuthId(), Role.ADMIN, 1);
//     }
//
//     @Test
//     @DisplayName("문의 목록 조회")
//     void getInquiryList() throws Exception {
//         // 기본 파라미터로 조회
//         mockMvc.perform(get("/admin/api/inquiries")
//                 .header("Authorization", adminToken)
//                 .param("page", "1")  // 페이지는 1부터 시작
//                 .param("size", "5"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.inquiries").isArray())
//                 .andExpect(jsonPath("$.inquiries[0].inquiry_id").value(testInquiry.getId()))
//                 .andExpect(jsonPath("$.inquiries[0].category").value("DEPOSIT"))
//                 .andExpect(jsonPath("$.inquiries[0].status").value("PENDING"));
//
//         // 카테고리와 상태로 필터링하여 조회
//         mockMvc.perform(get("/admin/api/inquiries")
//                 .header("Authorization", adminToken)
//                 .param("page", "1")
//                 .param("size", "5")
//                 .param("category", "DEPOSIT")
//                 .param("status", "PENDING"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.inquiries").isArray())
//                 .andExpect(jsonPath("$.inquiries[0].inquiry_id").value(testInquiry.getId()));
//
//         // 검색어로 조회
//         mockMvc.perform(get("/admin/api/inquiries")
//                 .header("Authorization", adminToken)
//                 .param("page", "1")
//                 .param("size", "5")
//                 .param("searchContent", "테스트"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.inquiries").isArray());
//     }
//
//     @Test
//     @DisplayName("문의 상세 조회")
//     void getInquiryDetail() throws Exception {
//         mockMvc.perform(get("/admin/api/inquiries/{inquiry-id}", testInquiry.getId())
//                 .header("Authorization", adminToken))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.inquiry_id").value(testInquiry.getId()))
//                 .andExpect(jsonPath("$.inquiry_content").value("테스트 문의 내용"))
//                 .andExpect(jsonPath("$.category").value("DEPOSIT"))
//                 .andExpect(jsonPath("$.tags[0]").value("test"))
//                 .andExpect(jsonPath("$.customer_name").value("테스트 고객"))
//                 .andExpect(jsonPath("$.phone_number").value("01012345678"));
//     }
//
//     @Test
//     @DisplayName("문의 답변 등록")
//     void registerInquiryResponse() throws Exception {
//         AdminInquiryResponseRequest request = AdminInquiryResponseRequest.of("답변 내용입니다.");
//
//         mockMvc.perform(post("/admin/api/inquiries/register/{inquiry-id}", testInquiry.getId())
//                 .header("Authorization", adminToken)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.inquiry_id").value(testInquiry.getId()))
//                 .andExpect(jsonPath("$.content").value("답변 내용입니다."))
//                 .andExpect(jsonPath("$.admin_id").value(testAdmin.getId()))
//                 .andExpect(jsonPath("$.created_at").exists());
//     }
//
//     @Test
//     @DisplayName("존재하지 않는 문의 상세 조회 시 404 응답")
//     void getInquiryDetail_NotFound() throws Exception {
//         mockMvc.perform(get("/admin/api/inquiries/{inquiry-id}", 999L)
//                 .header("Authorization", adminToken))
//                 .andExpect(status().isNotFound());
//     }
//
//     @Test
//     @DisplayName("인증되지 않은 요청 시 403 응답")
//     void getInquiryList_Unauthorized() throws Exception {
//         mockMvc.perform(get("/admin/api/inquiries")
//                 .param("page", "0")
//                 .param("size", "5"))
//                 .andExpect(status().isForbidden());
//     }
// }