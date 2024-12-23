package com.hanaro.schedule_hanaro.admin.service;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitStatusUpdateResponse;
import com.hanaro.schedule_hanaro.global.domain.*;
import com.hanaro.schedule_hanaro.global.domain.enums.*;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.CsVisitRepository;
import com.hanaro.schedule_hanaro.global.repository.SectionRepository;
import com.hanaro.schedule_hanaro.global.repository.VisitRepository;
import com.hanaro.schedule_hanaro.global.websocket.handler.WebsocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminVisitServiceTest {

    @InjectMocks
    private AdminVisitService adminVisitService;

    @Mock
    private VisitRepository visitRepository;
    @Mock
    private SectionRepository sectionRepository;
    @Mock
    private CsVisitRepository csVisitRepository;
    @Mock
    private WebsocketHandler websocketHandler;

    private Visit visit;
    private Section section;
    private Branch branch;
    private CsVisit csVisit;
    private Visit nextVisit;
    private Customer customer;

    @BeforeEach
    void setUp() {
        branch = Branch.builder()
                .name("테스트 지점")
                .branchType(BranchType.BANK)
                .xPosition("127.1234567")
                .yPosition("37.1234567")
                .address("서울시 테스트구 테스트동")
                .tel("02-1234-5678")
                .businessTime("09:00-16:00")
                .build();

        customer = Customer.builder()
                .authId("testuser")
                .password("password123")
                .name("테스트유저")
                .phoneNum("01012345678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .build();

        section = Section.builder()
                .branch(branch)
                .sectionType(SectionType.DEPOSIT)
                .build();
        ReflectionTestUtils.setField(section, "currentNum", 0);
        ReflectionTestUtils.setField(section, "waitAmount", 0);
        ReflectionTestUtils.setField(section, "waitTime", 0);

        visit = Visit.builder()
                .customer(customer)
                .section(section)
                .visitDate(LocalDate.now())
                .num(1)
                .category(Category.DEPOSIT)
                .build();

        nextVisit = Visit.builder()
                .customer(customer)
                .section(section)
                .visitDate(LocalDate.now())
                .num(2)
                .category(Category.LOAN)
                .build();

        csVisit = CsVisit.builder()
                .branch(branch)
                .date(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("방문 ID로 방문 정보를 찾을 수 있다")
    void findVisitById() {
        // given
        given(visitRepository.findById(1L)).willReturn(Optional.of(visit));

        // when
        Visit foundVisit = adminVisitService.findVisitById(1L);

        // then
        assertThat(foundVisit).isNotNull();
        assertThat(foundVisit).isEqualTo(visit);
    }

    @Test
    @DisplayName("방문 상태를 업데이트할 수 있다")
    void updateVisitStatus() {
        // given
        ReflectionTestUtils.setField(branch, "id", 1L);
        ReflectionTestUtils.setField(section, "id", 1L);
        ReflectionTestUtils.setField(visit, "id", 1L);
        ReflectionTestUtils.setField(visit, "status", Status.PENDING);
        
        // Mock repository methods
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(csVisitRepository.findByBranchIdAndDate(any(), any())).thenReturn(Optional.of(csVisit));
        when(visitRepository.findNextPendingVisit(any(), any())).thenReturn(Optional.of(nextVisit));
        
        // Mock save methods
        when(sectionRepository.save(any(Section.class))).thenReturn(section);
        when(csVisitRepository.save(any(CsVisit.class))).thenReturn(csVisit);

        // Mock websocket handler
        doNothing().when(websocketHandler).notifySubscribers(any(Long.class), any(String.class));

        // when
        AdminVisitStatusUpdateResponse response = adminVisitService.updateVisitStatus(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.currentNum()).isEqualTo(1);
        assertThat(response.currentCategory()).isEqualTo(Category.DEPOSIT.getCategory());
        assertThat(response.nextNum()).isEqualTo(2);
        assertThat(response.nextCategory()).isEqualTo(Category.LOAN.getCategory());
        
        // 상태 변경 확인
        assertThat(visit.getStatus()).isEqualTo(Status.PROGRESS);
        
        // 개별적으로 호출 검증
        verify(sectionRepository).save(any());
        verify(csVisitRepository).save(any());
        verify(visitRepository, times(1)).findById(1L);
        verify(visitRepository, times(1)).findNextPendingVisit(any(), any());
        verify(websocketHandler).notifySubscribers(any(Long.class), any(String.class));
    }

    @Test
    @DisplayName("현재 진행중인 방문 정보를 조회할 수 있다")
    void getCurrentVisit() {
        // given
        visit.changeStatusToProgress();
        
        given(sectionRepository.findById(1L)).willReturn(Optional.of(section));
        given(visitRepository.findCurrentProgressVisit(any(), any())).willReturn(Optional.of(visit));
        given(visitRepository.findNextPendingVisit(any(), any())).willReturn(Optional.of(nextVisit));
        given(csVisitRepository.findByBranchIdAndDate(any(), any())).willReturn(Optional.of(csVisit));

        // when
        AdminVisitStatusUpdateResponse response = adminVisitService.getCurrentVisit(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.currentNum()).isEqualTo(1);
        assertThat(response.currentCategory()).isEqualTo("예금");
        assertThat(response.nextNum()).isEqualTo(2);
        assertThat(response.nextCategory()).isEqualTo("대출");
        assertThat(response.sectionInfo()).satisfies(sectionInfo -> {
            assertThat(sectionInfo.sectionId()).isEqualTo(section.getId());
            assertThat(sectionInfo.sectionType()).isEqualTo("예금");
            assertThat(sectionInfo.currentNum()).isEqualTo(0);
            assertThat(sectionInfo.waitAmount()).isEqualTo(0);
            assertThat(sectionInfo.waitTime()).isEqualTo(0);
        });
    }

    @Test
    @DisplayName("존재하지 않는 방문 ID로 조회시 예외가 발생한다")
    void updateVisitStatus_WithInvalidId() {
        // given
        given(visitRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThrows(GlobalException.class, () -> adminVisitService.updateVisitStatus(999L));
    }

    @Test
    @DisplayName("PENDING이 아닌 상태에서는 상태 업데이트가 불가능하다")
    void updateVisitStatus_WhenStatusIsNotPending() {
        // given
        visit.changeStatusToProgress(); // 이미 PROGRESS 상태로 변경
        given(visitRepository.findById(1L)).willReturn(Optional.of(visit));

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, 
            () -> adminVisitService.updateVisitStatus(1L));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_PROGRESS);
    }

    @Test
    @DisplayName("섹션이 없는 방문은 상태 업데이트가 불가능하다")
    void updateVisitStatus_WhenSectionNotFound() {
        // given
        Visit visitWithoutSection = Visit.builder()
                .customer(customer)
                .visitDate(LocalDate.now())
                .num(1)
                .category(Category.DEPOSIT)
                .build();  // section은 null로 설정
        given(visitRepository.findById(1L)).willReturn(Optional.of(visitWithoutSection));

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, 
            () -> adminVisitService.updateVisitStatus(1L));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_SECTION);
    }

    @Test
    @DisplayName("지점이 없는 섹션은 상태 업데이트가 불가능하다")
    void updateVisitStatus_WhenBranchNotFound() {
        // given
        Section sectionWithoutBranch = Section.builder()
                .sectionType(SectionType.DEPOSIT)
                .build();  // branch는 null로 설정
        
        Visit visitWithoutBranch = Visit.builder()
                .customer(customer)
                .section(sectionWithoutBranch)
                .visitDate(LocalDate.now())
                .num(1)
                .category(Category.DEPOSIT)
                .build();
        
        given(visitRepository.findById(1L)).willReturn(Optional.of(visitWithoutBranch));

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, 
            () -> adminVisitService.updateVisitStatus(1L));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_BRANCH);
    }

    @Test
    @DisplayName("CsVisit 정보가 없으면 상태 업데이트가 불가능하다")
    void updateVisitStatus_WhenCsVisitNotFound() {
        // given
        ReflectionTestUtils.setField(branch, "id", 1L);  // branch ID 설정
        ReflectionTestUtils.setField(section, "id", 1L);  // section ID 설정
        
        given(visitRepository.findById(1L)).willReturn(Optional.of(visit));
        given(csVisitRepository.findByBranchIdAndDate(any(), any())).willReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, 
            () -> adminVisitService.updateVisitStatus(1L));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_CS_VISIT);
    }

    @Test
    @DisplayName("존재하지 않는 섹션의 현재 방문 조회시 예외가 발생한다")
    void getCurrentVisit_WhenSectionNotFound() {
        // given
        given(sectionRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, 
            () -> adminVisitService.getCurrentVisit(999L));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_SECTION);
    }

    @Test
    @DisplayName("진행중인 방문이 없는 경우 예외가 발생한다")
    void getCurrentVisit_WhenNoProgressVisit() {
        // given
        given(sectionRepository.findById(1L)).willReturn(Optional.of(section));
        given(visitRepository.findCurrentProgressVisit(any(), any())).willReturn(Optional.empty());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, 
            () -> adminVisitService.getCurrentVisit(1L));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_VISIT);
    }
}