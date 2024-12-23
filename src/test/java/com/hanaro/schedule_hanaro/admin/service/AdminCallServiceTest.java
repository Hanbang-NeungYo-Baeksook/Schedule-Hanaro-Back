package com.hanaro.schedule_hanaro.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.Collection;

import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
import com.hanaro.schedule_hanaro.global.domain.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallWaitResponse;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import com.hanaro.schedule_hanaro.global.repository.CallMemoRepository;
import com.hanaro.schedule_hanaro.global.repository.CallRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;

@ExtendWith(MockitoExtension.class)
class AdminCallServiceTest {

    @InjectMocks
    private AdminCallService adminCallService;

    @Mock
    private CallRepository callRepository;
    @Mock
    private InquiryRepository inquiryRepository;
    @Mock
    private CallMemoRepository callMemoRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private CustomerRepository customerRepository;

    private Customer customer;
    private Call call;
    private Admin admin;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
            .authId("test")
            .password("password")
            .name("홍길동")
            .phoneNum("01012345678")
            .birth(LocalDate.of(1990, 1, 1))
            .gender(Gender.MALE)
            .build();

        call = Call.builder()
            .customer(customer)
            .callDate(LocalDateTime.now())
            .callNum(1)
            .category(Category.DEPOSIT)
            .content("테스트 문의")
            .tags("태그1,태그2")
            .build();

        admin = Admin.builder()
            .authId("admin")
            .password("password")
            .name("관리자")
            .branch(Branch.builder()
                .name("테스트 지점")
                .branchType(BranchType.BANK)
                .xPosition("127.1234567")
                .yPosition("37.1234567")
                .address("서울시 테스트구 테스트동")
                .tel("02-1234-5678")
                .businessTime("09:00-16:00")
                .build())
            .build();
    }

    @Test
    @DisplayName("대기 목록 조회 테스트")
    void findWaitList() {
        // given
        when(callRepository.findByStatus(Status.PROGRESS)).thenReturn(List.of(call));
        when(callRepository.findByStatus(Status.PENDING)).thenReturn(List.of(call));
        when(callRepository.findCallHistoryByCustomerId(any(), any())).thenReturn(List.of());
        when(inquiryRepository.findByCustomerId(any())).thenReturn(List.of());

        // when
        AdminCallWaitResponse response = adminCallService.findWaitList();

        // then
        assertThat(response).isNotNull();
        verify(callRepository).findByStatus(Status.PROGRESS);
        verify(callRepository).findByStatus(Status.PENDING);
    }

    @Test
    @DisplayName("상담 상태 변경 테스트 - PENDING에서 PROGRESS로")
    void changeCallStatus_FromPendingToProgress() {
        // given
        call.setStatus(Status.PENDING);
        when(callRepository.findById(anyLong())).thenReturn(Optional.of(call));

        // when
        String result = adminCallService.changeCallStatus(1L);

        // then
        assertThat(result).isEqualTo("상담 진행 처리되었습니다.");
        verify(callRepository).updateStatus(anyLong(), eq(Status.PROGRESS));
    }

    @Test
    @DisplayName("상담 상태 변경 테스트 - PROGRESS에서 COMPLETE로")
    void changeCallStatus_FromProgressToComplete() {
        // given
        call.setStatus(Status.PROGRESS);
        when(callRepository.findById(anyLong())).thenReturn(Optional.of(call));

        // when
        String result = adminCallService.changeCallStatus(1L);

        // then
        assertThat(result).isEqualTo("상담 완료 처리되었습니다.");
        verify(callRepository).updateStatus(anyLong(), eq(Status.COMPLETE));
    }

    @Test
    @DisplayName("상담 상태 변경 테스트 - COMPLETE 상태에서 변경 시도")
    void changeCallStatus_FromComplete_ThrowsException() {
        // given
        call.setStatus(Status.COMPLETE);
        when(callRepository.findById(anyLong())).thenReturn(Optional.of(call));

        // when & then
        assertThatThrownBy(() -> adminCallService.changeCallStatus(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("이미 완료된 상담입니다.");
    }

    @Test
    @DisplayName("상담 메모 저장 테스트")
    void saveCallMemo() {
        // given
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        
        CustomUserDetails userDetails = new CustomUserDetails(
            3L,                    // id
            admin.getAuthId(),     // authId
            admin.getPassword(),   // password
            Role.ADMIN,           // role
            authorities           // authorities
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(callRepository.findById(anyLong())).thenReturn(Optional.of(call));
        when(adminRepository.findById(3L)).thenReturn(Optional.of(admin));

        // when
        String result = adminCallService.saveCallMemo(1L, "테스트 메모");

        // then
        assertThat(result).isEqualTo("Success");
        verify(callMemoRepository).save(any());
        
        // cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("필터링된 상담 목록 조회 테스트")
    void findFilteredCalls() {
        // given
        when(callRepository.findByFiltering(any(), any(), any(), any(), any(), any()))
            .thenReturn(new SliceImpl<>(List.of(call)));

        // when
        var result = adminCallService.findFilteredCalls(1, 10, Status.PENDING, 
            LocalDate.now(), LocalDate.now(), Category.DEPOSIT, "검색어");

        // then
        assertThat(result).isNotNull();
        assertThat(result.data()).isNotEmpty();
        verify(callRepository).findByFiltering(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("상담 조회 테스트")
    void findCall() {
        // given
        when(callRepository.findById(anyLong())).thenReturn(Optional.of(call));
        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));

        // when
        var result = adminCallService.findCall(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.callId()).isEqualTo(call.getId());
        assertThat(result.customerName()).isEqualTo(customer.getName());
    }

    @Test
    @DisplayName("존재하지 않는 상담 조회 시 예외 발생")
    void findCall_WithInvalidId_ThrowsException() {
        // given
        when(callRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCallService.findCall(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 상담입니다.");
    }
}