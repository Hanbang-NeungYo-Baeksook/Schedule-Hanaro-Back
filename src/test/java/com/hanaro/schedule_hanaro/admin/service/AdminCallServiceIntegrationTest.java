package com.hanaro.schedule_hanaro.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallWaitResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallHistoryListResponse;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import com.hanaro.schedule_hanaro.global.domain.CallMemo;
import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import com.hanaro.schedule_hanaro.global.repository.CallRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;

import jakarta.persistence.EntityManager;

import com.hanaro.schedule_hanaro.global.repository.CallMemoRepository;

@SpringBootTest
@Transactional
@Testcontainers
class AdminCallServiceIntegrationTest {

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
    private AdminCallService adminCallService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CallRepository callRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CallMemoRepository callMemoRepository;

    @Autowired
    private EntityManager entityManager;

    private Admin testAdmin;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        callMemoRepository.deleteAll();
        callRepository.deleteAll();
        customerRepository.deleteAll();
        adminRepository.deleteAll();

        // ID가 3인 Admin 생성
        testAdmin = Admin.builder()
            .authId("testAdmin")
            .password("password")
            .name("테스트 관리자")
            .build();
        
        // ID를 3L로 설정하기 위해 native query 사용
        entityManager.createNativeQuery("ALTER TABLE Admin AUTO_INCREMENT = 3").executeUpdate();
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
    }

    @Test
    @DisplayName("대기 목록 조회 테스트")
    void findWaitList_Success() {
        // given
        Call progressCall = createCall(Status.PROGRESS, 1);
        createCall(Status.PENDING, 2);  // pendingCall1
        createCall(Status.PENDING, 3);  // pendingCall2

        // when
        AdminCallWaitResponse response = adminCallService.findWaitList();

        // then
        assertThat(response.progress()).isNotNull();
        assertThat(response.progress().id()).isEqualTo(progressCall.getId());
        assertThat(response.waiting()).hasSize(2);
    }

    @Test
    @DisplayName("상담 상태 변경 테스트 - PENDING에서 PROGRESS로")
    void changeCallStatus_PendingToProgress() {
        // given
        Call pendingCall = createCall(Status.PENDING, 1);

        // when
        String result = adminCallService.changeCallStatus(pendingCall.getId());

        // then
        assertThat(result).isEqualTo("상담 진행 처리되었습니다.");
        Call updatedCall = callRepository.findById(pendingCall.getId()).orElseThrow();
        assertThat(updatedCall.getStatus()).isEqualTo(Status.PROGRESS);
    }

    @Test
    @DisplayName("상담 상태 변경 테스트 - PROGRESS에서 COMPLETE로")
    void changeCallStatus_ProgressToComplete() {
        // given
        Call progressCall = createCall(Status.PROGRESS, 1);

        // when
        String result = adminCallService.changeCallStatus(progressCall.getId());

        // then
        assertThat(result).isEqualTo("상담 완료 처리되었습니다.");
        Call updatedCall = callRepository.findById(progressCall.getId()).orElseThrow();
        assertThat(updatedCall.getStatus()).isEqualTo(Status.COMPLETE);
    }

    @Test
    @DisplayName("존재하지 않는 상담 상태 변경 시 예외 발생")
    void changeCallStatus_NotFound() {
        // when & then
        assertThrows(IllegalArgumentException.class, () ->
            adminCallService.changeCallStatus(999L)
        );
    }

    @Test
    @DisplayName("이미 완료된 상담 상태 변경 시 예외 발생")
    void changeCallStatus_AlreadyComplete() {
        // given
        Call completeCall = createCall(Status.COMPLETE, 1);

        // when & then
        assertThrows(IllegalStateException.class, () ->
            adminCallService.changeCallStatus(completeCall.getId())
        );
    }

    @Test
    @DisplayName("상담 메모 저장 성공")
    void saveCallMemo_Success() {
        // given
        Call call = createCall(Status.PROGRESS, 1);
        String content = "테스트 메모입니다.";

        // when
        String result = adminCallService.saveCallMemo(call.getId(), content);

        // then
        assertThat(result).isEqualTo("Success");
        
        List<CallMemo> savedMemos = callMemoRepository.findAll();
        assertThat(savedMemos).hasSize(1);
        assertThat(savedMemos.get(0).getContent()).isEqualTo(content);
        assertThat(savedMemos.get(0).getAdmin().getId()).isEqualTo(testAdmin.getId());
        assertThat(savedMemos.get(0).getCall().getId()).isEqualTo(call.getId());
    }

    @Test
    @DisplayName("존재하지 않는 상담에 대한 메모 저장 시 예외 발생")
    void saveCallMemo_NotFound() {
        // when & then
        assertThrows(IllegalArgumentException.class, () ->
            adminCallService.saveCallMemo(999L, "테스트 메모")
        );
    }

    private Call createCall(Status status, int callNum) {
        Call call = Call.builder()
                .customer(testCustomer)
                .callDate(LocalDateTime.now())
                .callNum(callNum)
                .category(Category.DEPOSIT)
                .content("테스트 콜 " + callNum)
                .startedAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now())
                .tags("test")
                .build();
        call.setStatus(status);
        return callRepository.saveAndFlush(call);
    }

    @Test
    @DisplayName("필터링된 상담 목록 조회 테스트")
    void findFilteredCalls_Success() {
        // given
        LocalDate today = LocalDate.now();
        createCall(Status.COMPLETE, 1);
        createCall(Status.COMPLETE, 2);

        // when
        AdminCallHistoryListResponse response = adminCallService.findFilteredCalls(
            1, 10, 
            Status.COMPLETE, 
            today, today,
            Category.DEPOSIT,
            ""
        );

        // then
        assertThat(response.data()).hasSize(2);
        assertThat(response.pagination().currentPage()).isEqualTo(1);
        assertThat(response.pagination().hasNext()).isFalse();
    }

    @Test
    @DisplayName("필터링된 상담 목록 - 날짜 범위로 검색")
    void findFilteredCalls_DateRange() {
        // given
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        createCall(Status.COMPLETE, 1);  // today
        createCall(Status.COMPLETE, 2);  // today
        
        // 어제 날짜로 상담 생성
        Call yesterdayCall = Call.builder()
                .customer(testCustomer)
                .callDate(yesterday.atStartOfDay())
                .callNum(3)
                .category(Category.DEPOSIT)
                .content("테스트 콜 3")
                .startedAt(yesterday.atStartOfDay())
                .endedAt(yesterday.atStartOfDay())
                .tags("test")
                .build();
        yesterdayCall.setStatus(Status.COMPLETE);
        callRepository.save(yesterdayCall);

        // when
        AdminCallHistoryListResponse response = adminCallService.findFilteredCalls(
            1, 10,
            Status.COMPLETE,
            yesterday, today,
            Category.DEPOSIT,
            ""
        );

        // then
        assertThat(response.data()).hasSize(3);
    }

    @Test
    @DisplayName("필터링된 상담 목록 - 키워드로 검색")
    void findFilteredCalls_WithKeyword() {
        // given
        Call call1 = createCallWithContent("테스트 키워드 검색");
        createCallWithContent("다른 내용의 상담");

        // when
        AdminCallHistoryListResponse response = adminCallService.findFilteredCalls(
            1, 10,
            Status.COMPLETE,
            LocalDate.now(), LocalDate.now(),
            Category.DEPOSIT,
            "키워드"
        );

        // then
        assertThat(response.data()).hasSize(1);
        assertThat(response.data().get(0).id()).isEqualTo(call1.getId());
    }

    private Call createCallWithContent(String content) {
        Call call = Call.builder()
                .customer(testCustomer)
                .callDate(LocalDateTime.now())
                .callNum(1)
                .category(Category.DEPOSIT)
                .content(content)
                .startedAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now())
                .tags("test")
                .build();
        call.setStatus(Status.COMPLETE);
        return callRepository.save(call);
    }

    @Test
    @DisplayName("페이지네이션 테스트")
    void findFilteredCalls_Pagination() {
        // given
        LocalDate today = LocalDate.now();
        
        // 15개의 테스트 데이터 생성
        for (int i = 0; i < 15; i++) {
            createCall(Status.COMPLETE, i + 1);
        }

        // when
        AdminCallHistoryListResponse firstPage = adminCallService.findFilteredCalls(
            1,      // 현재 페이지
            10,     // 페이지 크기
            Status.COMPLETE,
            today,  // 시작일
            today,  // 종료일
            Category.DEPOSIT,
            ""     // 검색 키워드
        );

        AdminCallHistoryListResponse secondPage = adminCallService.findFilteredCalls(
            2,      // 현재 페이지
            10,     // 페이지 크기
            Status.COMPLETE,
            today,  // 시작일
            today,  // 종료일
            Category.DEPOSIT,
            ""     // 검색 키워드
        );

        // then
        // 첫 페이지 검증
        assertThat(firstPage.data()).hasSize(10);
        assertThat(firstPage.pagination().currentPage()).isEqualTo(1);
        assertThat(firstPage.pagination().pageSize()).isEqualTo(10);
        assertThat(firstPage.pagination().hasNext()).isTrue();

        // 두 번째 페이지 검증
        assertThat(secondPage.data()).hasSize(5);
        assertThat(secondPage.pagination().currentPage()).isEqualTo(2);
        assertThat(secondPage.pagination().pageSize()).isEqualTo(10);
        assertThat(secondPage.pagination().hasNext()).isFalse();
    }
}