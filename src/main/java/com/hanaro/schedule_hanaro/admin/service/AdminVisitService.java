package com.hanaro.schedule_hanaro.admin.service;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitStatusUpdateResponse;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.CsVisitRepository;
import com.hanaro.schedule_hanaro.global.repository.SectionRepository;
import com.hanaro.schedule_hanaro.global.repository.VisitRepository;
import com.hanaro.schedule_hanaro.global.domain.CsVisit;
import com.hanaro.schedule_hanaro.global.domain.Visit;
import com.hanaro.schedule_hanaro.global.websocket.handler.WebsocketHandler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminVisitService {

    private final VisitRepository visitRepository;
    private final SectionRepository sectionRepository;

    private final WebsocketHandler websocketHandler;

    public Visit findVisitById(Long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_VISIT));
    }

    private final CsVisitRepository csVisitRepository;


    @Transactional
    public AdminVisitStatusUpdateResponse updateVisitStatus(Long visitId) {
        if (visitId == null) {
            throw new GlobalException(ErrorCode.INVALID_VISIT_NUMBER);
        }

        // 1. 현재 방문 조회
        Visit currentVisit = visitRepository.findByIdWithPessimisticLock(visitId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_VISIT));

        if (currentVisit.getStatus() != Status.PENDING) {
            throw new GlobalException(ErrorCode.ALREADY_PROGRESS, "Visit ID: " + visitId);
        }

        // 2. 섹션 조회
        Section section = sectionRepository.findByIdWithPessimisticLock(currentVisit.getSection().getId())
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_SECTION));

        // 3. CS 방문 조회
        CsVisit csVisit = csVisitRepository.findByBranchIdAndDateWithPessimisticLock(
                section.getBranch().getId(), LocalDate.now())
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CS_VISIT));
        
        // 4. 이전 상태 저장
        Visit previousVisit = visitRepository.findPreviousVisit(currentVisit.getNum())
            .orElse(null);
        int previousNum = previousVisit != null ? previousVisit.getNum() : 0;
        String previousCategory = previousVisit != null ? previousVisit.getCategory().getCategory() : "";

        if(previousVisit != null) {
            previousVisit.changeStatusToCompleted();
            visitRepository.save(previousVisit);
        }

        // 5. 현재 방문 상태 변경
        currentVisit.changeStatusToProgress();
        int estimatedWaitTime = currentVisit.getCategory().getWaitTime();

        // 6. PENDING 상태인 방문 수 계산
        List<Visit> pendingVisits = visitRepository.findNextPendingVisitsWithPessimisticLock(
            section.getId(),
            Status.PENDING
        );
        int pendingCount = pendingVisits.size();

        // 7. 섹션 상태 업데이트
        section.updateStatusPendingToProgress(currentVisit.getNum(), estimatedWaitTime, pendingCount);
        sectionRepository.save(section);

    
        // 8. 다음 대기 방문 조회 (이미 조회한 pendingVisits에서 첫 번째 항목 사용)
        Optional<Visit> nextVisitOpt = visitRepository.findNextPendingVisit(section.getId(), Status.PENDING);

        int nextNum = nextVisitOpt.map(Visit::getNum).orElse(0);
        String nextCategory = nextVisitOpt.map(visit -> visit.getCategory().getCategory()).orElse("");


        
        // 9. 웹소켓 메시지 전송
        log.debug("===== WebSocket Message =====");
        log.debug("Section ID: {}", section.getId());
        log.debug("Message: VISIT_UPDATE:{}", section.getId());
        log.debug("=========================");

        websocketHandler.notifySubscribers(section.getId(), 
            String.format("VISIT_UPDATE:%d", section.getId()));

        // 10. CS 방문 업데이트
        csVisit.increaseTotalNum();
        csVisitRepository.save(csVisit);

        // 11. 응답 생성
        AdminVisitStatusUpdateResponse.SectionInfo sectionInfo = AdminVisitStatusUpdateResponse.SectionInfo.builder()
            .sectionId(section.getId())
            .sectionType(section.getSectionType().toString())
            .currentNum(section.getCurrentNum())
            .waitAmount(section.getWaitAmount())
            .waitTime(section.getWaitTime())
            .todayVisitors(csVisit.getTotalNum())
            .build();

        return AdminVisitStatusUpdateResponse.builder()
            .previousNum(previousNum)
            .previousCategory(previousCategory)
            .currentNum(currentVisit.getNum())
            .currentCategory(currentVisit.getCategory().getCategory())
            .nextNum(nextNum)
            .nextCategory(nextCategory)
            .sectionInfo(sectionInfo)
            .build();
    }

    private AdminVisitStatusUpdateResponse.SectionInfo createSectionInfo(Section section, CsVisit csVisit) {
        return AdminVisitStatusUpdateResponse.SectionInfo.builder()
                .sectionId(section.getId())
                .sectionType(section.getSectionType().getType())
                .currentNum(section.getCurrentNum())
                .waitAmount(section.getWaitAmount())
                .waitTime(section.getWaitTime())
                .todayVisitors(csVisit.getTotalNum())
                .build();
    }

    @Transactional
    public AdminVisitStatusUpdateResponse getCurrentVisit(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_SECTION));

        Visit currentVisit = visitRepository.findCurrentProgressVisitByDate(Status.PROGRESS)
                .orElse(null);

        CsVisit csVisit = csVisitRepository.findByBranchIdAndDate(section.getBranch().getId(), LocalDate.now())
                .orElseGet(() -> {
                    CsVisit newCsVisit = CsVisit.builder()
                            .branch(section.getBranch())
                            .date(LocalDate.now())
                            .build();
                    return csVisitRepository.save(newCsVisit);
                });

        List<Visit> pendingVisits = visitRepository.findNextPendingVisitsWithPessimisticLock(
            sectionId, 
            Status.PENDING
        );
        int pendingCount = pendingVisits.size();
        
        int estimatedWaitTime = currentVisit != null ? 
            currentVisit.getCategory().getWaitTime() : 0;

        section.updateStatusPendingToProgress(
            currentVisit != null ? currentVisit.getNum() : 0,
            estimatedWaitTime,
            pendingCount
        );
        sectionRepository.save(section);

        Visit previousVisit = visitRepository.findPreviousVisitByDate(currentVisit.getNum())
                .orElse(null);

        Visit nextVisit = visitRepository.findNextPendingVisitByDate(Status.PENDING)
            .orElse(null);

        AdminVisitStatusUpdateResponse.SectionInfo sectionInfo = createSectionInfo(section, csVisit);

        return AdminVisitStatusUpdateResponse.builder()
                .previousNum(previousVisit != null ? previousVisit.getNum() : 0)
                .previousCategory(previousVisit != null ? previousVisit.getCategory().getCategory() : "")
                .currentNum(currentVisit != null ? currentVisit.getNum() : 0)
                .currentCategory(currentVisit != null ? currentVisit.getCategory().getCategory() : "")
                .nextNum(nextVisit != null ? nextVisit.getNum() : 0)
                .nextCategory(nextVisit != null ? nextVisit.getCategory().getCategory() : "")
                .sectionInfo(sectionInfo)
                .build();
    }
}
