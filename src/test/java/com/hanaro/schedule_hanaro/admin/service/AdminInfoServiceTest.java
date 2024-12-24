// package com.hanaro.schedule_hanaro.admin.service;
//
// import com.hanaro.schedule_hanaro.admin.dto.response.AdminInfoResponse;
// import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
// import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
// import com.hanaro.schedule_hanaro.global.domain.Admin;
// import com.hanaro.schedule_hanaro.global.exception.GlobalException;
// import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
// import com.hanaro.schedule_hanaro.global.repository.CallRepository;
// import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.core.Authentication;
// import org.springframework.test.util.ReflectionTestUtils;
//
// import java.util.Optional;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.BDDMockito.given;
//
//
// @ExtendWith(MockitoExtension.class)
// class AdminInfoServiceTest {
//
//     @InjectMocks
//     private AdminInfoService adminInfoService;
//
//     @Mock
//     private AdminRepository adminRepository;
//     @Mock
//     private InquiryRepository inquiryRepository;
//     @Mock
//     private CallRepository callRepository;
//     @Mock
//     private Authentication authentication;
//
//     private Admin admin;
//     private AdminInquiryStatsDto phoneStats;
//     private AdminInquiryStatsDto inquiryStats;
//
//     @BeforeEach
//     void setUp() {
//         admin = Admin.builder()
//                 .authId("testAdmin")
//                 .password("password")
//                 .name("테스트 관리자")
//                 .build();
//
//         // Reflection을 사용하여 테스트용 ID 설정
//         ReflectionTestUtils.setField(admin, "id", 1L);
//
//         // AdminInquiryStatsDto 객체 생성 (today, weekly, monthly, total)
//         phoneStats = new AdminInquiryStatsDto(5, 20, 80, 100);  // 오늘 5건, 주간 20건, 월간 80건, 전체 100건
//         inquiryStats = new AdminInquiryStatsDto(3, 15, 60, 150);  // 오늘 3건, 주간 15건, 월간 60건, 전체 150건
//     }
//
//     @Test
//     @DisplayName("관리자 통계 정상 조회")
//     void getAdminStats_Success() {
//         // given
//         CustomUserDetails userDetails = CustomUserDetails.of(
//             admin.getId(),
//             admin.getAuthId(),
//             admin.getPassword(),
//             admin.getRole()
//         );
//         given(authentication.getPrincipal()).willReturn(userDetails);
//         given(adminRepository.findById(1L)).willReturn(Optional.of(admin));
//         given(callRepository.getStatsByAdminId(1L)).willReturn(phoneStats);
//         given(inquiryRepository.getStatsByAdminId(1L)).willReturn(inquiryStats);
//
//         // when
//         AdminInfoResponse response = adminInfoService.getAdminStats(authentication);
//
//         // then
//         assertThat(response).isNotNull();
//         assertThat(response.adminId()).isEqualTo(admin.getId());
//         assertThat(response.adminInfo().name()).isEqualTo(admin.getName());
//
//         // 전화 상담 통계 검증
//         assertThat(response.phoneInquiryStats().today()).isEqualTo(5);
//         assertThat(response.phoneInquiryStats().weekly()).isEqualTo(20);
//         assertThat(response.phoneInquiryStats().monthly()).isEqualTo(80);
//         assertThat(response.phoneInquiryStats().total()).isEqualTo(100);
//
//         // 1:1 문의 통계 검증
//         assertThat(response.oneToOneInquiryStats().today()).isEqualTo(3);
//         assertThat(response.oneToOneInquiryStats().weekly()).isEqualTo(15);
//         assertThat(response.oneToOneInquiryStats().monthly()).isEqualTo(60);
//         assertThat(response.oneToOneInquiryStats().total()).isEqualTo(150);
//     }
//
//     @Test
//     @DisplayName("인증 정보가 없는 경우 예외 발생")
//     void getAdminStats_WithNullAuthentication() {
//         // when & then
//         assertThrows(GlobalException.class, () ->
//             adminInfoService.getAdminStats(null)
//         );
//     }
//
//     @Test
//     @DisplayName("존재하지 않는 관리자 ID로 조회시 예외 발생")
//     void getAdminStats_WithNonExistentAdmin() {
//         // given
//         CustomUserDetails userDetails = CustomUserDetails.of(
//             admin.getId(),
//             admin.getAuthId(),
//             admin.getPassword(),
//             admin.getRole()
//         );
//         given(authentication.getPrincipal()).willReturn(userDetails);
//         given(adminRepository.findById(any())).willReturn(Optional.empty());
//
//         // when & then
//         assertThrows(GlobalException.class, () ->
//             adminInfoService.getAdminStats(authentication)
//         );
//     }
//
//     @Test
//     @DisplayName("전화 상담 통계가 없는 경우 예외 발생")
//     void getAdminStats_WithNullPhoneStats() {
//         // given
//         CustomUserDetails userDetails = CustomUserDetails.of(
//             admin.getId(),
//             admin.getAuthId(),
//             admin.getPassword(),
//             admin.getRole()
//         );
//         given(authentication.getPrincipal()).willReturn(userDetails);
//         given(adminRepository.findById(1L)).willReturn(Optional.of(admin));
//         given(callRepository.getStatsByAdminId(1L)).willReturn(null);
//
//         // when & then
//         assertThrows(GlobalException.class, () ->
//             adminInfoService.getAdminStats(authentication)
//         );
//     }
//
//     @Test
//     @DisplayName("1:1 문의 통계가 없는 경우 예외 발생")
//     void getAdminStats_WithNullInquiryStats() {
//         // given
//         CustomUserDetails userDetails = CustomUserDetails.of(
//             admin.getId(),
//             admin.getAuthId(),
//             admin.getPassword(),
//             admin.getRole()
//         );
//         given(authentication.getPrincipal()).willReturn(userDetails);
//         given(adminRepository.findById(1L)).willReturn(Optional.of(admin));
//         given(callRepository.getStatsByAdminId(1L)).willReturn(phoneStats);
//         given(inquiryRepository.getStatsByAdminId(1L)).willReturn(null);
//
//         // when & then
//         assertThrows(GlobalException.class, () ->
//             adminInfoService.getAdminStats(authentication)
//         );
//     }
// }