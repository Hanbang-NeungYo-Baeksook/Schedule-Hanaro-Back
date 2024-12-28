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
        log.info("=== Start updateVisitStatus Service ===");
        log.info("Visit ID: {}", visitId);

        if (visitId == null) {
            throw new GlobalException(ErrorCode.INVALID_VISIT_NUMBER);
        }

        // 1. 현재 방문 조회
        Visit currentVisit = visitRepository.findByIdWithPessimisticLock(visitId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_VISIT));
        log.info("Current Visit - ID: {}, Status: {}", currentVisit.getId(), currentVisit.getStatus());

        if (currentVisit.getStatus() != Status.PENDING) {
            throw new GlobalException(ErrorCode.ALREADY_PROGRESS, "Visit ID: " + visitId);
        }

        // 2. 섹션 조회
        Section section = sectionRepository.findByIdWithPessimisticLock(currentVisit.getSection().getId())
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_SECTION));
        log.info("Section - ID: {}", section.getId());

        // 3. CS 방문 조회
        CsVisit csVisit = csVisitRepository.findByBranchIdAndDate(
                section.getBranch().getId(), LocalDate.now())
            .orElseGet(() -> {
                CsVisit newCsVisit = CsVisit.builder()
                        .branch(section.getBranch())
                        .date(LocalDate.now())
                        .build();
                return csVisitRepository.save(newCsVisit);
            });
        
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
        log.info("=== Finding Pending Visits ===");
        log.info("Section ID: {}", section.getId());
        log.info("Status value: {}", Status.PENDING);
        
        log.info("Querying visits - sectionId: {}, status: {}, status.name: {}", 
            section.getId(), 
            Status.PENDING, 
            Status.PENDING.name()
        );

        List<Visit> pendingVisits = visitRepository.findNextPendingVisitsWithPessimisticLock(
            section.getId(),
            Status.PENDING
        );
        log.info("=== Pending Visits Info ===");
        log.info("Section ID: {}", section.getId());
        log.info("Status: {}", Status.PENDING);
        log.info("Found visits count: {}", pendingVisits.size());
        pendingVisits.forEach(visit -> 
            log.info("Visit[{}] - Status: {}, Date: {}", 
                visit.getId(), visit.getStatus(), visit.getVisitDate())
        );
        int pendingCount = pendingVisits.size();

        // 7. 섹션 상태 업데이트
        section.updateStatusPendingToProgress(currentVisit.getNum(), estimatedWaitTime, pendingCount);
        sectionRepository.save(section);

        // 8. 다음 대기 방문 조회
        Visit nextVisit = visitRepository.findNextPendingVisitByDate(Status.PENDING)
            .orElse(null);

        int nextNum = nextVisit != null ? nextVisit.getNum() : 0;
        String nextCategory = nextVisit != null ? nextVisit.getCategory().getCategory() : "";
        
        // 9. 웹소켓 메시지 전송
        log.debug("===== WebSocket Message =====");
        log.debug("Section ID: {}", section.getId());
        log.debug("Message: VISIT_UPDATE:{}", section.getId());
        log.debug("=========================");

        try {
            log.info("=== 웹소켓 메시지 전송 시작 ===");
            log.info("Visit ID: {}", visitId);
            log.info("Branch ID: {}", section.getBranch().getId());
            
            String message = String.format("VISIT_UPDATE:%d", section.getBranch().getId());
            log.info("Sending websocket message: {}", message);
            
            websocketHandler.notifySubscribers(section.getBranch().getId(), message);
            
            log.info("=== 웹소켓 메시지 전송 완료 ===");
        } catch (Exception e) {
            log.error("웹소켓 메시지 전송 실패", e);
            log.error("Error details:", e);
        }

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
        // Section 조회
        Section section = sectionRepository.findByIdWithPessimisticLock(sectionId)
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
        log.info("=== getCurrentVisit Pending Visits Info ===");
        log.info("Section ID: {}", sectionId);
        log.info("Found pending visits: {}", pendingVisits.size());
        pendingVisits.forEach(visit -> 
            log.info("Visit[{}] - Status: {}, Date: {}", 
                visit.getId(), visit.getStatus(), visit.getVisitDate())
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

        Visit previousVisit = currentVisit != null ? 
            visitRepository.findPreviousVisitByDate(currentVisit.getNum())
                .orElse(null) : null;

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
