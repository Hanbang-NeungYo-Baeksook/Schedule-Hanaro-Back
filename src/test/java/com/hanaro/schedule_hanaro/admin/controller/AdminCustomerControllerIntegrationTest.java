package com.hanaro.schedule_hanaro.admin.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
import com.hanaro.schedule_hanaro.global.auth.provider.JwtTokenProvider;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;
import com.hanaro.schedule_hanaro.global.domain.enums.Role;
import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class AdminCustomerControllerIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Admin testAdmin;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        inquiryRepository.deleteAll();
        customerRepository.deleteAll();
        adminRepository.deleteAll();

        // 테스트 관리자 생성
        testAdmin = Admin.builder()
                .authId("testAdmin")
                .password(new BCryptPasswordEncoder().encode("password"))
                .name("테스트 관리자")
                .build();
        ReflectionTestUtils.setField(testAdmin, "role", Role.ADMIN);
        testAdmin = adminRepository.save(testAdmin);

        // 테스트 고객 생성
        testCustomer = Customer.builder()
                .authId("test@test.com")
                .password("password")
                .name("테스트 고객")
                .phoneNum("01012345678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .build();
        testCustomer = customerRepository.save(testCustomer);

        // 테스트 문의 생성
        Inquiry testInquiry = Inquiry.builder()
                .customer(testCustomer)
                .content("테스트 문의 내용")
                .inquiryNum(1)
                .category(Category.DEPOSIT)
                .status(InquiryStatus.PENDING)
                .tags("test")
                .build();
        inquiryRepository.save(testInquiry);
    }

    private String createTestToken() {
        return jwtTokenProvider.generateToken(
            testAdmin.getAuthId(),
            Role.ADMIN,
            1
        );
    }

    @Test
    @DisplayName("고객 목록 조회")
    void getCustomerList() throws Exception {
        mockMvc.perform(get("/admin/api/customers")
                .header("Authorization", "Bearer " + createTestToken())
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers").isArray())
                .andExpect(jsonPath("$.customers[0].email").value("test@test.com"))
                .andExpect(jsonPath("$.customers[0].customer_name").value("테스트 고객"))
                .andExpect(jsonPath("$.customers[0].phone_number").value("01012345678"))
                .andExpect(jsonPath("$.customers[0].birth_date").value("1990-01-01"))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.page_size").value(10))
                .andExpect(jsonPath("$.total_pages").value(1));
    }

    @Test
    @DisplayName("고객 상세 정보 조회")
    void getCustomerInfo() throws Exception {
        mockMvc.perform(get("/admin/api/customers/{customer-id}", testCustomer.getId())
                .header("Authorization", "Bearer " + createTestToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer_id").value(testCustomer.getId()))
                .andExpect(jsonPath("$.name").value("테스트 고객"))
                .andExpect(jsonPath("$.phone").value("01012345678"))
                .andExpect(jsonPath("$.birth").value("1990-01-01"))
                .andExpect(jsonPath("$.auth_id").value("test@test.com"));
    }

    @Test
    @DisplayName("고객 문의 이력 조회")
    void getCustomerInquiries() throws Exception {
        mockMvc.perform(get("/admin/api/customers/{customer-id}/content", testCustomer.getId())
                .header("Authorization", "Bearer " + createTestToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone_inquiries").isArray())
                .andExpect(jsonPath("$.one_to_one_inquiries").isArray())
                .andExpect(jsonPath("$.one_to_one_inquiries[0].inquiry_id").exists())
                .andExpect(jsonPath("$.one_to_one_inquiries[0].content").value("테스트 문의 내용"))
                .andExpect(jsonPath("$.one_to_one_inquiries[0].category").value("DEPOSIT"))
                .andExpect(jsonPath("$.one_to_one_inquiries[0].status").value("PENDING"))
                .andExpect(jsonPath("$.one_to_one_inquiries[0].created_at").exists());
    }

    @Test
    @DisplayName("존재하지 않는 고객 상세 정보 조회 시 404 응답")
    void getCustomerInfo_NotFound() throws Exception {
        mockMvc.perform(get("/admin/api/customers/{customer-id}", 999L)
                .header("Authorization", "Bearer " + createTestToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("인증되지 않은 요청 시 403 응답")
    void getCustomerList_Unauthorized() throws Exception {
        mockMvc.perform(get("/admin/api/customers")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("잘못된 토큰으로 요청 시 401 응답")
    void getCustomerList_InvalidToken() throws Exception {
        // 올바른 JWT 형식이지만 유효하지 않은 토큰 사용
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + 
                            "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
                            "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        mockMvc.perform(get("/admin/api/customers")
                .header("Authorization", "Bearer " + invalidToken)
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("페이지네이션 파라미터 검증")
    void getCustomerList_Pagination() throws Exception {
        // 페이지 크기가 0일 때
        mockMvc.perform(get("/admin/api/customers")
                .header("Authorization", "Bearer " + createTestToken())
                .param("page", "1")
                .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("페이지 크기는 1 이상이어야 합니다."));

        // 페이지 번호가 0일 때
        mockMvc.perform(get("/admin/api/customers")
                .header("Authorization", "Bearer " + createTestToken())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isBadRequest());

        // 페이지 크기가 최대값을 초과할 때
        mockMvc.perform(get("/admin/api/customers")
                .header("Authorization", "Bearer " + createTestToken())
                .param("page", "1")
                .param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("고객 문의 이력이 ��는 경우")
    void getCustomerInquiries_Empty() throws Exception {
        // 새로운 고객 생성 (문의 없음)
        Customer newCustomer = Customer.builder()
                .authId("new@test.com")
                .password("password")
                .name("새로운 고객")
                .phoneNum("01087654321")
                .birth(LocalDate.of(1995, 1, 1))
                .gender(Gender.FEMALE)
                .build();
        newCustomer = customerRepository.save(newCustomer);

        mockMvc.perform(get("/admin/api/customers/{customer-id}/content", newCustomer.getId())
                .header("Authorization", "Bearer " + createTestToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone_inquiries").isArray())
                .andExpect(jsonPath("$.phone_inquiries").isEmpty())
                .andExpect(jsonPath("$.one_to_one_inquiries").isArray())
                .andExpect(jsonPath("$.one_to_one_inquiries").isEmpty());
    }

    @Test
    @DisplayName("고객 목록 조회 - 검색 조건 테스트")
    void getCustomerList_WithSearchConditions() throws Exception {
        mockMvc.perform(get("/admin/api/customers")
                .header("Authorization", "Bearer " + createTestToken())
                .param("page", "1")
                .param("size", "10")
                .param("keyword", "테스트")
                .param("startDate", LocalDate.now().minusDays(1).toString())
                .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers").isArray());
    }

    @Test
    @DisplayName("고객 문의 이력 조회 - 정렬 순서 확인")
    void getCustomerInquiries_SortOrder() throws Exception {
        // 추가 문의 생성
        Inquiry newInquiry = Inquiry.builder()
                .customer(testCustomer)
                .content("새로운 문의 내용")
                .inquiryNum(2)
                .category(Category.DEPOSIT)
                .status(InquiryStatus.PENDING)
                .tags("new")
                .build();
        inquiryRepository.save(newInquiry);

        mockMvc.perform(get("/admin/api/customers/{customer-id}/content", testCustomer.getId())
                .header("Authorization", "Bearer " + createTestToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.one_to_one_inquiries").isArray())
                .andExpect(jsonPath("$.one_to_one_inquiries.length()").value(2))
                // 최신 문의가 먼저 오는지 확인
                .andExpect(jsonPath("$.one_to_one_inquiries[0].content").value("새로운 문의 내용"));
    }
}