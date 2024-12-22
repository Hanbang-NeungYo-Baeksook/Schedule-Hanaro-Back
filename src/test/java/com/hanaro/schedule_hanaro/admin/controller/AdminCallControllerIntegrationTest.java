package com.hanaro.schedule_hanaro.admin.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaro.schedule_hanaro.admin.dto.request.AdminCallMemoRequest;
import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
import com.hanaro.schedule_hanaro.global.auth.provider.JwtAuthenticationProvider;
import com.hanaro.schedule_hanaro.global.auth.provider.JwtTokenProvider;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
import com.hanaro.schedule_hanaro.global.domain.enums.Role;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import com.hanaro.schedule_hanaro.global.repository.CallRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;


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

    @Autowired
    private EntityManager entityManager;

    private Admin testAdmin;
    private Customer testCustomer;
    private Call testCall;

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
                .content("테스트 콜")
                .startedAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now())
                .tags("test")
                .build();
        testCall.setStatus(Status.PENDING);
        testCall = callRepository.save(testCall);
    }

    private CustomUserDetails createTestUserDetails() {
        return CustomUserDetails.of(
            testAdmin.getId(),
            testAdmin.getAuthId(),
            testAdmin.getPassword(),
            Role.ADMIN
        );
    }

    private String createTestToken() {
        return jwtTokenProvider.generateToken(
            testAdmin.getAuthId(),  // admin_id와 동일한 authId 사용
            Role.ADMIN,
            1
        );
    }

    @Test
    @DisplayName("전화 상담 메모 등록")
    @WithMockUser(roles = "ADMIN")
    void postCallMemo() throws Exception {
        // given
        Long callId = testCall.getId();
        AdminCallMemoRequest request = new AdminCallMemoRequest("테스트 메모 내용");
        
        CustomUserDetails userDetails = CustomUserDetails.of(
            testAdmin.getId(),
            String.valueOf(testAdmin.getId()),
            testAdmin.getPassword(),
            Role.ADMIN
        );

        // when & then
        mockMvc.perform(post("/admin/api/calls/{call-id}", callId)
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("전화 상담 상태 변경 - PENDING to PROGRESS")
    void patchCallStatus_PendingToProgress() throws Exception {
        // given
        Long callId = testCall.getId();
        
        // when & then
        mockMvc.perform(patch("/admin/api/calls/{call-id}", callId)
                .header("Authorization", "Bearer " + createTestToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("상담 진행 처리되었습니다."));
        
        // EntityManager를 통해 캐시를 비우고 다시 조회
        entityManager.clear();
        
        Call updatedCall = callRepository.findById(callId).orElseThrow();
        assertThat(updatedCall.getStatus()).isEqualTo(Status.PROGRESS);
    }

    @Test
    @DisplayName("전화 상담 상태 변경 - PROGRESS to COMPLETE")
    void patchCallStatus_ProgressToComplete() throws Exception {
        // given
        Long callId = testCall.getId();
        
        // 먼저 PROGRESS 상태로 변경
        testCall.setStatus(Status.PROGRESS);
        callRepository.save(testCall);
        entityManager.flush();
        entityManager.clear();
        
        // when & then
        mockMvc.perform(patch("/admin/api/calls/{call-id}", callId)
                .header("Authorization", "Bearer " + createTestToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("상담 완료 처리되었습니다."));
        
        // EntityManager를 통해 캐시를 비우고 다시 조회
        entityManager.clear();
        
        Call updatedCall = callRepository.findById(callId).orElseThrow();
        assertThat(updatedCall.getStatus()).isEqualTo(Status.COMPLETE);
    }

    @Test
    @DisplayName("전화 상담 목록 조회")
    void getCallList() throws Exception {
        // given
        Long callId = testCall.getId();

        // when & then
        mockMvc.perform(get("/admin/api/calls")
                .header("Authorization", "Bearer " + createTestToken())
                .param("status", Status.PENDING.name())
                .param("page", "1")
                .param("size", "5")
                .param("category", Category.DEPOSIT.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(callId))
                .andExpect(jsonPath("$.data[0].content").value("테스트 콜"))
                .andExpect(jsonPath("$.data[0].category").value("DEPOSIT"))
                .andExpect(jsonPath("$.pagination.currentPage").value(1))
                .andExpect(jsonPath("$.pagination.pageSize").value(5))
                .andExpect(jsonPath("$.pagination.hasNext").isBoolean());
    }

    @Test
    @DisplayName("전화 상담 상세 조회")
    void getCallDetail() throws Exception {
        // given
        Long callId = testCall.getId();

        // when & then
        mockMvc.perform(get("/admin/api/calls/{call-id}", callId)
                .header("Authorization", "Bearer " + createTestToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.call_id").value(callId))
                .andExpect(jsonPath("$.content").value("테스트 콜"))
                .andExpect(jsonPath("$.category").value("DEPOSIT"))
                .andExpect(jsonPath("$.customer_name").value("테스트 고객"))
                .andExpect(jsonPath("$.mobile").value("01012345678"));
    }

    @Test
    @DisplayName("전화 상담 대기 목록 조회")
    void getCallWaitList() throws Exception {
        // given
        Long callId = testCall.getId();

        // when & then
        mockMvc.perform(get("/admin/api/calls/wait")
                .header("Authorization", "Bearer " + createTestToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").isEmpty())  // 진행 중인 상담이 없으므로 null
                .andExpect(jsonPath("$.waiting").isArray())   // 대기 목록은 배열
                .andExpect(jsonPath("$.waiting[0].id").value(callId))
                .andExpect(jsonPath("$.waiting[0].category").value("DEPOSIT"))
                .andExpect(jsonPath("$.waiting[0].content").value("테스트 콜"))
                .andExpect(jsonPath("$.waiting[0].user_name").value("테스트 고객"))
                .andExpect(jsonPath("$.waiting[0].mobile").value("01012345678"))
                .andExpect(jsonPath("$.waiting[0].calls").isArray())
                .andExpect(jsonPath("$.waiting[0].inquires").isArray())
                .andExpect(jsonPath("$.waiting[0].waiting_num").value(1));
    }
}