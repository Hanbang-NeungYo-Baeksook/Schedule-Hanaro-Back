// package com.hanaro.schedule_hanaro.admin.service;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;
// import java.util.Collections;
// import java.util.Collection;
//
// import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
// import com.hanaro.schedule_hanaro.global.domain.*;
// import com.hanaro.schedule_hanaro.global.domain.enums.*;
// import com.hanaro.schedule_hanaro.global.websocket.handler.WebsocketHandler;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.domain.SliceImpl;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.test.util.ReflectionTestUtils;
//
// import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallWaitResponse;
// import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
// import com.hanaro.schedule_hanaro.global.repository.CallMemoRepository;
// import com.hanaro.schedule_hanaro.global.repository.CallRepository;
// import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
// import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
// import com.hanaro.schedule_hanaro.global.exception.GlobalException;
// import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
//
// @ExtendWith(MockitoExtension.class)
// class AdminCallServiceTest {
//
//     @InjectMocks
//     private AdminCallService adminCallService;
//
//     @Mock
//     private CallRepository callRepository;
//     @Mock
//     private InquiryRepository inquiryRepository;
//     @Mock
//     private CallMemoRepository callMemoRepository;
//     @Mock
//     private AdminRepository adminRepository;
//     @Mock
//     private CustomerRepository customerRepository;
//     @Mock
//     private WebsocketHandler websocketHandler;
//
//     private Customer customer;
//     private Call call;
//     private Admin admin;
//
//     @BeforeEach
//     void setUp() {
//         customer = Customer.builder()
//             .authId("test")
//             .password("password")
//             .name("홍길동")
//             .phoneNum("01012345678")
//             .birth(LocalDate.of(1990, 1, 1))
//             .gender(Gender.MALE)
//             .build();
//
//         call = Call.builder()
//             .customer(customer)
//             .callDate(LocalDateTime.now())
//             .callNum(1)
//             .category(Category.DEPOSIT)
//             .content("테스트 문의")
//             .tags("태그1,태그2")
//             .build();
//         ReflectionTestUtils.setField(call, "id", 1L);
//
//         admin = Admin.builder()
//             .authId("admin")
//             .password("password")
//             .name("관리자")
//             .branch(Branch.builder()
//                 .name("테스트 지점")
//                 .branchType(BranchType.BANK)
//                 .xPosition("127.1234567")
//                 .yPosition("37.1234567")
//                 .address("서울시 테스트구 테스트동")
//                 .tel("02-1234-5678")
//                 .businessTime("09:00-16:00")
//                 .build())
//             .build();
//         ReflectionTestUtils.setField(admin, "id", 1L);
//     }
//
//     private Authentication createMockAuthentication() {
//         Collection<GrantedAuthority> authorities = Collections.singletonList(
//             new SimpleGrantedAuthority("ROLE_ADMIN")
//         );
//
//         CustomUserDetails userDetails = new CustomUserDetails(
//             admin.getId(),
//             admin.getAuthId(),
//             admin.getPassword(),
//             Role.ADMIN,
//             authorities
//         );
//
//         Authentication authentication = mock(Authentication.class);
//         when(authentication.getPrincipal()).thenReturn(userDetails);
//         return authentication;
//     }
//
//     @Test
//     @DisplayName("대기 목록 조회 테스트")
//     void findWaitList() {
//         // given
//         when(callRepository.findByStatus(Status.PROGRESS))
//             .thenReturn(List.of(call));
//         when(callRepository.findByStatus(Status.PENDING))
//             .thenReturn(List.of(call));
//         when(callRepository.findByCustomerIdAndIdNotAndStatus(
//             any(), any(), any()))
//             .thenReturn(List.of());
//         when(inquiryRepository.findByCustomerId(any()))
//             .thenReturn(List.of());
//
//         // when
//         AdminCallWaitResponse response = adminCallService.findWaitList();
//
//         // then
//         assertThat(response).isNotNull();
//         verify(callRepository).findByStatus(Status.PROGRESS);
//         verify(callRepository).findByStatus(Status.PENDING);
//     }
//
//     @Test
//     @DisplayName("상담 상태를 진행 중으로 변경")
//     void changeCallStatusProgress() {
//         // given
//         Authentication authentication = createMockAuthentication();
//
//         call.setStatus(Status.PENDING);
//         when(adminRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
//         when(callRepository.findFirstByStatusOrderByCallNumAsc(Status.PENDING))
//             .thenReturn(Optional.of(call));
//         doNothing().when(websocketHandler).notifySubscribers(anyLong(), anyString());
//
//         // when
//         Long result = adminCallService.changeCallStatusProgress(authentication);
//
//         // then
//         assertThat(result).isEqualTo(call.getId());
//         verify(callRepository).updateStatusWithStartedAt(eq(call.getId()), eq(Status.PROGRESS), any());
//         verify(callMemoRepository).save(any());
//         verify(websocketHandler).notifySubscribers(anyLong(), anyString());
//     }
//
//     @Test
//     @DisplayName("상담 상태를 완료로 변경")
//     void changeCallStatusComplete() {
//         // given
//         call.setStatus(Status.PROGRESS);
//         when(callRepository.findById(anyLong())).thenReturn(Optional.of(call));
//
//         // when
//         String result = adminCallService.changeCallStatusComplete(1L);
//
//         // then
//         assertThat(result).isEqualTo("상담 완료 처리되었습니다.");
//         verify(callRepository).updateStatusWithEndedAt(anyLong(), eq(Status.COMPLETE), any());
//     }
//
//     @Test
//     @DisplayName("잘못된 상태에서 완료로 변경 시도시 예외 발생")
//     void changeCallStatusComplete_WithWrongStatus_ThrowsException() {
//         // given
//         call.setStatus(Status.PENDING);
//         when(callRepository.findById(anyLong())).thenReturn(Optional.of(call));
//
//         // when & then
//         assertThatThrownBy(() -> adminCallService.changeCallStatusComplete(1L))
//             .isInstanceOf(GlobalException.class)
//             .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WRONG_CALL_STATUS);
//     }
//
//     @Test
//     @DisplayName("상담 메모 저장 테스트")
//     void saveCallMemo() {
//         // given
//         Authentication authentication = createMockAuthentication();
//
//         when(callRepository.findById(anyLong())).thenReturn(Optional.of(call));
//         when(adminRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
//         when(callMemoRepository.findByCallId(anyLong())).thenReturn(null);
//
//         // when
//         String result = adminCallService.saveCallMemo(authentication, 1L, "테스트 메모");
//
//         // then
//         assertThat(result).isEqualTo("Success");
//         verify(callMemoRepository).save(any());
//     }
//
//     @Test
//     @DisplayName("이미 메모가 있는 경우 업데이트 테스트")
//     void saveCallMemo_WithExistingMemo() {
//         // given
//         Collection<GrantedAuthority> authorities = Collections.singletonList(
//             new SimpleGrantedAuthority("ROLE_ADMIN")
//         );
//
//         Authentication authentication = new UsernamePasswordAuthenticationToken(
//             new CustomUserDetails(admin.getId(), admin.getAuthId(), admin.getPassword(), Role.ADMIN, authorities),
//             null,
//             authorities
//         );
//
//         CallMemo existingMemo = CallMemo.builder()
//             .id(1L)
//             .call(call)
//             .admin(admin)
//             .content("")
//             .build();
//
//         when(callRepository.findById(anyLong())).thenReturn(Optional.of(call));
//         when(callMemoRepository.findByCallId(anyLong())).thenReturn(existingMemo);
//
//         // when
//         String result = adminCallService.saveCallMemo(authentication, 1L, "테스트 메모");
//
//         // then
//         assertThat(result).isEqualTo("Success");
//         verify(callMemoRepository).save(any());
//     }
//
//     @Test
//     @DisplayName("이미 내용이 있는 메모 업데이트 시도시 예외 발생")
//     void saveCallMemo_WithNonEmptyMemo_ThrowsException() {
//         // given
//         Collection<GrantedAuthority> authorities = Collections.singletonList(
//             new SimpleGrantedAuthority("ROLE_ADMIN")
//         );
//
//         Authentication authentication = new UsernamePasswordAuthenticationToken(
//             new CustomUserDetails(admin.getId(), admin.getAuthId(), admin.getPassword(), Role.ADMIN, authorities),
//             null,
//             authorities
//         );
//
//         CallMemo existingMemo = CallMemo.builder()
//             .id(1L)
//             .call(call)
//             .admin(admin)
//             .content("기존 메모")
//             .build();
//
//         when(callRepository.findById(anyLong())).thenReturn(Optional.of(call));
//         when(callMemoRepository.findByCallId(anyLong())).thenReturn(existingMemo);
//
//         // when & then
//         assertThatThrownBy(() -> adminCallService.saveCallMemo(authentication, 1L, "테스트 메모"))
//             .isInstanceOf(GlobalException.class)
//             .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_POST_MEMO);
//     }
//
//     @Test
//     @DisplayName("필터링된 상담 목록 조회 테스트")
//     void findFilteredCalls() {
//         // given
//         when(callRepository.findByFiltering(any(), any(), any(), any(), any(), any()))
//             .thenReturn(new SliceImpl<>(List.of(call)));
//
//         // when
//         var result = adminCallService.findFilteredCalls(1, 10, Status.PENDING,
//             LocalDate.now(), LocalDate.now(), Category.DEPOSIT, "검색어");
//
//         // then
//         assertThat(result).isNotNull();
//         assertThat(result.data()).isNotEmpty();
//         verify(callRepository).findByFiltering(any(), any(), any(), any(), any(), any());
//     }
//
//     @Test
//     @DisplayName("상담 조회 테스트")
//     void findCall() {
//         // given
//         CallMemo callMemo = CallMemo.builder()
//             .id(1L)
//             .call(call)
//             .admin(admin)
//             .content("테스트 메모")
//             .build();
//
//         when(callRepository.findById(anyLong())).thenReturn(Optional.of(call));
//         when(customerRepository.findById(any())).thenReturn(Optional.of(customer));
//         when(callMemoRepository.findByCallId(anyLong())).thenReturn(callMemo);
//
//         // when
//         var result = adminCallService.findCall(1L);
//
//         // then
//         assertThat(result).isNotNull();
//         assertThat(result.callId()).isEqualTo(call.getId());
//         assertThat(result.customerName()).isEqualTo(customer.getName());
//         assertThat(result.content()).isEqualTo(call.getContent());
//         assertThat(result.replyContent()).isEqualTo("테스트 메모");
//     }
//
//     @Test
//     @DisplayName("메모가 없는 상담 조회 테스트")
//     void findCall_WithoutMemo() {
//         // given
//         when(callRepository.findById(anyLong())).thenReturn(Optional.of(call));
//         when(customerRepository.findById(any())).thenReturn(Optional.of(customer));
//         when(callMemoRepository.findByCallId(anyLong())).thenReturn(null);
//
//         // when
//         var result = adminCallService.findCall(1L);
//
//         // then
//         assertThat(result).isNotNull();
//         assertThat(result.callId()).isEqualTo(call.getId());
//         assertThat(result.customerName()).isEqualTo(customer.getName());
//         assertThat(result.content()).isEqualTo(call.getContent());
//         assertThat(result.replyContent()).isNull();
//     }
//
//     @Test
//     @DisplayName("존재하지 않는 상담 조회 시 예외 발생")
//     void findCall_WithInvalidId_ThrowsException() {
//         // given
//         when(callRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//         // when & then
//         assertThatThrownBy(() -> adminCallService.findCall(1L))
//             .isInstanceOf(GlobalException.class)
//             .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND_CALL);
//     }
// }
