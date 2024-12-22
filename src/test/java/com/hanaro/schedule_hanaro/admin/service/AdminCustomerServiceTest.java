package com.hanaro.schedule_hanaro.admin.service;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInquiryListResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerListResponse;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.CallRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCustomerServiceTest {

    @InjectMocks
    private AdminCustomerService adminCustomerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CallRepository callRepository;

    @Mock
    private InquiryRepository inquiryRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .authId("test@test.com")
                .password("password")
                .name("테스트")
                .phoneNum("01012345678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .build();
    }

    @Test
    @DisplayName("고객 정보 조회 성공")
    void findCustomerInfoById_Success() {
        // given
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));

        // when
        AdminCustomerInfoResponse response = adminCustomerService.findCustomerInfoById(customerId);

        // then
        assertThat(response.name()).isEqualTo(testCustomer.getName());
        assertThat(response.authId()).isEqualTo(testCustomer.getAuthId());
        assertThat(response.phone()).isEqualTo(testCustomer.getPhoneNum());
    }

    @Test
    @DisplayName("고객 정보 조회 실패 - 존재하지 않는 고객")
    void findCustomerInfoById_NotFound() {
        // given
        Long customerId = 999L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(GlobalException.class, () -> 
            adminCustomerService.findCustomerInfoById(customerId)
        );
    }

    @Test
    @DisplayName("고객 정보 조회 실패 - null ID")
    void findCustomerInfoById_NullId() {
        assertThrows(GlobalException.class, () ->
            adminCustomerService.findCustomerInfoById(null)
        );
    }

    @Test
    @DisplayName("고객 목록 조회 성공")
    void getCustomerList_Success() {
        // given
        int page = 1;
        int size = 10;
        List<Customer> customers = List.of(testCustomer);
        Page<Customer> customerPage = new PageImpl<>(customers);
        
        when(customerRepository.findAll(any(PageRequest.class))).thenReturn(customerPage);

        // when
        AdminCustomerListResponse response = adminCustomerService.getCustomerList(page, size);

        // then
        assertThat(response.customers()).hasSize(1);
        assertThat(response.currentPage()).isEqualTo(1);
        assertThat(response.totalItems()).isEqualTo(1);
    }

    @Test
    @DisplayName("고객 문록 조회 실패 - 잘못된 페이지 번호")
    void getCustomerList_InvalidPage() {
        assertThrows(GlobalException.class, () ->
            adminCustomerService.getCustomerList(0, 10)
        );
    }

    @Test
    @DisplayName("고객 목록 조회 실패 - 잘못된 페이지 크기")
    void getCustomerList_InvalidSize() {
        assertThrows(GlobalException.class, () ->
            adminCustomerService.getCustomerList(1, 0)
        );
    }

    @Test
    @DisplayName("고객 목록 조회 실패 - 빈 결과")
    void getCustomerList_EmptyResult() {
        // given
        when(customerRepository.findAll(any(PageRequest.class)))
            .thenReturn(new PageImpl<>(new ArrayList<>()));

        // when & then
        assertThrows(GlobalException.class, () ->
            adminCustomerService.getCustomerList(1, 10)
        );
    }

    @Test
    @DisplayName("고객 문의 목록 조회 성공")
    void findCustomerInquiryList_Success() {
        // given
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(callRepository.findByCustomerId(customerId)).thenReturn(new ArrayList<>());
        when(inquiryRepository.findAllByCustomerId(customerId)).thenReturn(new ArrayList<>());

        // when
        AdminCustomerInquiryListResponse response = 
            adminCustomerService.findCustomerInquiryList(customerId);

        // then
        assertThat(response.phoneInquiries()).isEmpty();
        assertThat(response.oneToOneInquiries()).isEmpty();
    }

    @Test
    @DisplayName("고객 문의 목록 조회 실패 - 존재하지 않는 고객")
    void findCustomerInquiryList_CustomerNotFound() {
        // given
        Long customerId = 999L;
        when(customerRepository.existsById(customerId)).thenReturn(false);

        // when & then
        assertThrows(GlobalException.class, () ->
            adminCustomerService.findCustomerInquiryList(customerId)
        );
    }

    @Test
    @DisplayName("고객 문의 목록 조회 실패 - null ID")
    void findCustomerInquiryList_NullId() {
        assertThrows(GlobalException.class, () ->
            adminCustomerService.findCustomerInquiryList(null)
        );
    }

    @Test
    @DisplayName("고객 문의 목록 조회 - Call 카테고리 null 체크")
    void findCustomerInquiryList_NullCallCategory() {
        // given
        Long customerId = 1L;
        Call callWithNullCategory = Call.builder()
            .customer(testCustomer)
            .callDate(LocalDateTime.now())
            .callNum(1)
            .category(null)  // null 카테고리
            .content("테스트 콜")
            .startedAt(LocalDateTime.now())
            .endedAt(LocalDateTime.now())
            .tags("")
            .build();
        
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(callRepository.findByCustomerId(customerId)).thenReturn(List.of(callWithNullCategory));
        when(inquiryRepository.findAllByCustomerId(customerId)).thenReturn(new ArrayList<>());

        // when & then
        assertThrows(GlobalException.class, () ->
            adminCustomerService.findCustomerInquiryList(customerId)
        );
    }

    @Test
    @DisplayName("고객 문의 목록 조회 - Inquiry 상태 null 체크")
    void findCustomerInquiryList_NullInquiryStatus() {
        // given
        Long customerId = 1L;
        Inquiry inquiryWithNullStatus = Inquiry.builder()
            .customer(testCustomer)
            .content("테스트 문의")
            .inquiryNum(1)
            .category(null)  // null 카테고리
            .status(null)    // null 상태
            .tags("")
            .build();
        
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(callRepository.findByCustomerId(customerId)).thenReturn(new ArrayList<>());
        when(inquiryRepository.findAllByCustomerId(customerId)).thenReturn(List.of(inquiryWithNullStatus));

        // when & then
        assertThrows(GlobalException.class, () ->
            adminCustomerService.findCustomerInquiryList(customerId)
        );
    }
}