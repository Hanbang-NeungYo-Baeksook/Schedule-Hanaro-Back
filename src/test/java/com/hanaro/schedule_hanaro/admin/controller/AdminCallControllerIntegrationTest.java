package com.hanaro.schedule_hanaro.admin.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaro.schedule_hanaro.global.auth.provider.JwtTokenProvider;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import com.hanaro.schedule_hanaro.global.domain.enums.Role;
import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.repository.CallRepository;
import com.hanaro.schedule_hanaro.admin.dto.request.AdminCallMemoRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class AdminCallControllerIntegrationTest {

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
    private CallRepository callRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Admin testAdmin;
    private Customer testCustomer;
    private Call testCall;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        callRepository.deleteAll();
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

        // 테스트 전화 상담 생성
        testCall = Call.builder()
                .customer(testCustomer)
                .callDate(LocalDateTime.now())
                .callNum(1)
                .category(Category.DEPOSIT)
                .content("테스트 상담 내용")
                .tags("test")
                .build();
        testCall = callRepository.save(testCall);

        // 관리자 토큰 생성
        adminToken = "Bearer " + jwtTokenProvider.generateToken(testAdmin.getAuthId(), Role.ADMIN, 1);
    }

    @Test
    @DisplayName("전화 상담 대기 목록 조회")
    void getCallWaitList() throws Exception {
        mockMvc.perform(get("/admin/api/calls/wait")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calls").isArray())
                .andExpect(jsonPath("$.calls[0].call_id").value(testCall.getId()))
                .andExpect(jsonPath("$.calls[0].category").value("DEPOSIT"))
                .andExpect(jsonPath("$.calls[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("전화 상담 진행 중으로 상태 변경")
    void patchCallStatusProgress() throws Exception {
        mockMvc.perform(patch("/admin/api/calls/{call-id}", testCall.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("상담 진행 처리되었습니다."));

        // 상태가 변경되었는지 확인
        mockMvc.perform(get("/admin/api/calls/" + testCall.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.PROGRESS.name()));
    }

    @Test
    @DisplayName("전화 상담 완료로 상태 변경")
    void patchCallStatusComplete() throws Exception {
        // 먼저 진행 중 상태로 변경
        mockMvc.perform(patch("/admin/api/calls/{call-id}", testCall.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("상담 진행 처리되었습니다."));

        // 완료 상태로 변경
        mockMvc.perform(patch("/admin/api/calls/{call-id}", testCall.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("상담 완료 처리되었습니다."));

        // 상태가 변경되었는지 확인
        mockMvc.perform(get("/admin/api/calls/" + testCall.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.COMPLETE.name()));
    }

    @Test
    @DisplayName("잘못된 상태에서 상담 완료 시도 시 에러")
    void patchCallStatusComplete_WrongStatus() throws Exception {
        // PENDING 상태에서 바로 완료로 변경 시도
        mockMvc.perform(patch("/admin/api/calls/{call-id}", testCall.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("대기 중인 상담이 없을 때 진행 중으로 상태 변경 시도")
    void patchCallStatusProgress_EmptyWaits() throws Exception {
        // 먼저 진행 중 상태로 변경
        mockMvc.perform(patch("/admin/api/calls/progress")
                .header("Authorization", adminToken))
                .andExpect(status().isOk());

        // 대기 중�� 상담이 없는 상태에서 다시 시도
        mockMvc.perform(patch("/admin/api/calls/progress")
                .header("Authorization", adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("전화 상담 메모 등록")
    void postCallMemo() throws Exception {
        AdminCallMemoRequest request = new AdminCallMemoRequest("상담 메모 내용입니다.");

        mockMvc.perform(post("/admin/api/calls/{call-id}", testCall.getId())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));
    }

    @Test
    @DisplayName("전화 상담 목록 조회")
    void getCallList() throws Exception {
        mockMvc.perform(get("/admin/api/calls")
                .header("Authorization", adminToken)
                .param("page", "1")
                .param("size", "5")
                .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calls").isArray())
                .andExpect(jsonPath("$.calls[0].call_id").value(testCall.getId()))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.total_pages").exists());
    }

    @Test
    @DisplayName("전화 상담 상세 조회")
    void getCallDetail() throws Exception {
        mockMvc.perform(get("/admin/api/calls/{call-id}", testCall.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.call_id").value(testCall.getId()))
                .andExpect(jsonPath("$.category").value("DEPOSIT"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("존재하지 않는 전화 상담 조회 시 404 응답")
    void getCallDetail_NotFound() throws Exception {
        mockMvc.perform(get("/admin/api/calls/{call-id}", 999L)
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("인증되지 않은 요청 시 403 응답")
    void getCallList_Unauthorized() throws Exception {
        mockMvc.perform(get("/admin/api/calls")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("이미 완료된 상담 상태 변경 시 409 응답")
    void patchCallStatus_AlreadyCompleted() throws Exception {
        // 먼저 진행 중 상태로 변경
        mockMvc.perform(patch("/admin/api/calls/{call-id}", testCall.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk());

        // 완료 상태로 변경
        mockMvc.perform(patch("/admin/api/calls/{call-id}", testCall.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk());

        // 다시 상태 변경 시도
        mockMvc.perform(patch("/admin/api/calls/{call-id}", testCall.getId())
                .header("Authorization", adminToken))
                .andExpect(status().isConflict())
                .andExpect(content().string("이미 완료된 상담입니다."));
    }
}