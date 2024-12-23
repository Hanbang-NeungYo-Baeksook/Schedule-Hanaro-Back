package com.hanaro.schedule_hanaro.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.hanaro.schedule_hanaro.admin.dto.request.AdminInquiryResponseRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.Authentication;
import static org.mockito.Mockito.mock;
import com.hanaro.schedule_hanaro.global.utils.PrincipalUtils;

import com.hanaro.schedule_hanaro.admin.dto.request.AdminInquiryListRequest;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryDetailResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryListResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryResponse;
import com.hanaro.schedule_hanaro.global.domain.*;
import com.hanaro.schedule_hanaro.global.domain.enums.*;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.*;
import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;

@ExtendWith(MockitoExtension.class)
class AdminInquiryServiceTest {

    @InjectMocks
    private AdminInquiryService adminInquiryService;

    @Mock
    private InquiryRepository inquiryRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private InquiryResponseRepository inquiryResponseRepository;

    private Customer createCustomer() {
        return Customer.builder()
            .name("테스트 고객")
            .phoneNum("01012345678")
            .build();
    }

    private Inquiry createInquiry(Customer customer) {
        Inquiry inquiry = Inquiry.builder()
            .customer(customer)
            .content("테스트 문의 내용")
            .inquiryNum(1)
            .category(Category.DEPOSIT)
            .status(InquiryStatus.PENDING)
            .tags("예금,상담")
            .build();

        ReflectionTestUtils.setField(inquiry, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(inquiry, "updatedAt", LocalDateTime.now());

        return inquiry;
    }

    private Admin createAdmin() {
        Branch branch = Branch.builder()
            .name("테스트 지점")
            .branchType(BranchType.BANK)
            .xPosition("127.1234567")
            .yPosition("37.1234567")
            .address("서울시 테스트구")
            .tel("02-1234-5678")
            .businessTime("09:00-16:00")
            .build();

        return Admin.builder()
            .authId("testadmin")
            .password("password123")
            .name("관리자")
            .branch(branch)
            .build();
    }

    @Nested
    @DisplayName("문의 목록 조회 테스트")
    class FindInquiryListTest {

        @Test
        @DisplayName("문의 목록 조회 성공")
        void findInquiryList_Success() {
            // given
            AdminInquiryListRequest request = AdminInquiryListRequest.from(
                "PENDING",
                Category.DEPOSIT,
                "",
                0,
                5
            );

            Customer customer = createCustomer();
            Inquiry inquiry = createInquiry(customer);
            List<Inquiry> inquiries = List.of(inquiry);
            Page<Inquiry> inquiryPage = new PageImpl<>(inquiries);

            given(inquiryRepository.findFilteredInquiries(
                anyString(), anyString(), anyString(), any()))
                .willReturn(inquiryPage);

            // when
            AdminInquiryListResponse response = adminInquiryService.findInquiryList(request);

            // then
            assertThat(response.inquiryList()).hasSize(1);
            assertThat(response.inquiryList().get(0).category()).isEqualTo(Category.DEPOSIT.toString());
            assertThat(response.inquiryList().get(0).status()).isEqualTo(InquiryStatus.PENDING.toString());
        }

        @Test
        @DisplayName("잘못된 상태값으로 조회 시 빈 목록 반환")
        void findInquiryList_WithInvalidStatus() {
            // given
            AdminInquiryListRequest request = AdminInquiryListRequest.from(
                "INVALID_STATUS",
                Category.DEPOSIT,
                "",
                0,
                5
            );

            Page<Inquiry> emptyPage = new PageImpl<>(List.of());

            given(inquiryRepository.findFilteredInquiries(
                anyString(), anyString(), anyString(), any()))
                .willReturn(emptyPage);

            // when
            AdminInquiryListResponse response = adminInquiryService.findInquiryList(request);

            // then
            assertThat(response.inquiryList()).isEmpty();
        }
    }

    @Nested
    @DisplayName("문의 상세 조회 테스트")
    class FindInquiryDetailTest {

        @Test
        @DisplayName("문의 상세 조회 성공 - 답변 없는 경우")
        void findInquiryDetail_Success_WithoutResponse() {
            // given
            Long inquiryId = 1L;
            Customer customer = createCustomer();
            Inquiry inquiry = createInquiry(customer);

            given(inquiryRepository.findInquiryDetailById(inquiryId))
                .willReturn(Optional.of(inquiry));
            given(inquiryResponseRepository.findByInquiryId(inquiryId))
                .willReturn(Optional.empty());

            // when
            AdminInquiryDetailResponse response = adminInquiryService.findInquiryDetail(inquiryId);

            // then
            assertThat(response.inquiryContent()).isEqualTo("테스트 문의 내용");
            assertThat(response.category()).isEqualTo(Category.DEPOSIT.toString());
            assertThat(response.replyContent()).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 문의 조회 시 예외 발생")
        void findInquiryDetail_NotFound() {
            // given
            Long inquiryId = 999L;
            given(inquiryRepository.findInquiryDetailById(inquiryId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminInquiryService.findInquiryDetail(inquiryId))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_INQUIRY);
        }

        @Test
        @DisplayName("문의 상세 조회 성공 - 답변 있는 경우")
        void findInquiryDetail_Success_WithResponse() {
            // given
            Long inquiryId = 1L;
            Customer customer = createCustomer();
            Inquiry inquiry = createInquiry(customer);
            Admin admin = createAdmin();

            InquiryResponse inquiryResponse = InquiryResponse.builder()
                .inquiry(inquiry)
                .admin(admin)
                .content("답변 내용입니다.")
                .createdAt(LocalDateTime.now())
                .build();

            given(inquiryRepository.findInquiryDetailById(inquiryId))
                .willReturn(Optional.of(inquiry));
            given(inquiryResponseRepository.findByInquiryId(inquiryId))
                .willReturn(Optional.of(inquiryResponse));

            // when
            AdminInquiryDetailResponse response = adminInquiryService.findInquiryDetail(inquiryId);

            // then
            assertThat(response.inquiryContent()).isEqualTo("테스트 문의 내용");
            assertThat(response.replyContent()).isEqualTo("답변 내용입니다.");
            assertThat(response.replyCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("문의 답변 등록 테스트")
    class RegisterInquiryResponseTest {

        private CustomUserDetails createCustomUserDetails(Admin admin) {
            return CustomUserDetails.of(
                admin.getId(),
                admin.getAuthId(),
                admin.getPassword(),
                Role.ADMIN
            );
        }

        @Test
        @DisplayName("문의 답변 등록 성공")
        void registerInquiryResponse_Success() {
            // given
            Long inquiryId = 1L;
            Long adminId = 1L;
            String content = "답변 내용입니다.";
            Authentication authentication = mock(Authentication.class);
            Admin admin = createAdmin();
            ReflectionTestUtils.setField(admin, "id", adminId);
            
            CustomUserDetails userDetails = createCustomUserDetails(admin);
            given(authentication.getPrincipal()).willReturn(userDetails);

            Customer customer = createCustomer();
            Inquiry inquiry = createInquiry(customer);
            ReflectionTestUtils.setField(inquiry, "id", inquiryId);

            InquiryResponse inquiryResponse = InquiryResponse.builder()
                .inquiry(inquiry)
                .admin(admin)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

            given(inquiryRepository.findById(inquiryId)).willReturn(Optional.of(inquiry));
            given(adminRepository.findById(adminId)).willReturn(Optional.of(admin));
            given(inquiryResponseRepository.findByInquiryId(inquiryId)).willReturn(Optional.empty());
            given(inquiryResponseRepository.save(any(InquiryResponse.class))).willReturn(inquiryResponse);

            // when
            AdminInquiryResponse response = adminInquiryService.registerInquiryResponse(inquiryId, content, authentication);

            // then
            assertThat(response.inquiryId()).isEqualTo(inquiryId);
            assertThat(response.adminId()).isEqualTo(adminId);
            assertThat(response.content()).isEqualTo(content);
        }

        @Test
        @DisplayName("존재하지 않는 문의에 답변 등록 시 예외 발생")
        void registerInquiryResponse_InquiryNotFound() {
            // given
            Long inquiryId = 999L;
            String content = "답변 내용";
            Authentication authentication = mock(Authentication.class);
            given(inquiryRepository.findById(inquiryId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminInquiryService.registerInquiryResponse(inquiryId, content, authentication))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_INQUIRY);
        }

        @Test
        @DisplayName("빈 답변 내용으로 등록 시 예외 발생")
        void registerInquiryResponse_EmptyContent() {
            // given
            Long inquiryId = 1L;
            String content = "";
            Authentication authentication = mock(Authentication.class);

            // when & then
            assertThatThrownBy(() -> adminInquiryService.registerInquiryResponse(inquiryId, content, authentication))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WRONG_REQUEST_PARAMETER);
        }

        @Test
        @DisplayName("너무 긴 답변 내용으로 등록 시 예외 발생")
        void registerInquiryResponse_ContentTooLong() {
            // given
            Long inquiryId = 1L;
            String content = "a".repeat(501);
            Authentication authentication = mock(Authentication.class);

            // when & then
            assertThatThrownBy(() -> adminInquiryService.registerInquiryResponse(inquiryId, content, authentication))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WRONG_REQUEST_PARAMETER);
        }
    }
}