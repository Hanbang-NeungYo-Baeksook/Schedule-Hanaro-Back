package com.hanaro.schedule_hanaro.admin.service;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitCarouselDto;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitNumResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitStatisticsDto;
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

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminVisitService {

    private int angle = 0;
    private final VisitRepository visitRepository;
    private final SectionRepository sectionRepository;

    private final WebsocketHandler websocketHandler;

    public Visit findVisitById(Long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_VISIT));
    }

    private final CsVisitRepository csVisitRepository;

    public AdminVisitNumResponse getVisitPageData(Long visitId) {
        if (visitId == null) {
            throw new GlobalException(ErrorCode.INVALID_VISIT_NUMBER, "방문 ID가 필요합니다.");
        }

        CsVisit visit = csVisitRepository.findById(visitId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CS_VISIT));

        if (visit.getTotalNum() < 0) {
            throw new GlobalException(ErrorCode.INVALID_TOTAL_VISITOR_COUNT);
        }

        visit.increase();

        List<Integer> numbers = getUpdatedCarouselNumbers(visit);

        List<Integer> displayNum = calculateDisplayNum(numbers);

        this.angle += 45;

        AdminVisitCarouselDto carouselDto = AdminVisitCarouselDto.builder()
                .numbers(numbers)
                .angle(this.angle)
                .displayNum(displayNum)
                .build();

        AdminVisitStatisticsDto statisticsDto = AdminVisitStatisticsDto.builder()
                .expectedWaitingCount(numbers.size())
                .estimatedWaitingTime(numbers.size() * 5)
                .todayVisitors(visit.getTotalNum())
                .build();

        return AdminVisitNumResponse.builder()
                .carousel(carouselDto)
                .statistics(statisticsDto)
                .build();
    }

    private List<Integer> getUpdatedCarouselNumbers(CsVisit visit) {
        if (visit == null) {
            throw new GlobalException(ErrorCode.NOT_FOUND_CS_VISIT);
        }

        if (visit.getTotalNum() < 0) {
            throw new GlobalException(ErrorCode.INVALID_TOTAL_VISITOR_COUNT);
        }

        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            numbers.add(0);
        }

        if (numbers.stream().allMatch(num -> num == 0)) {
            for (int i = 0; i < numbers.size(); i++) {
                numbers.set(i, visit.getTotalNum() + i);
            }
        } else {
            int lastNumber = numbers.remove(numbers.size() - 1);
            numbers.add(0, lastNumber + 1);
        }

        return numbers;
    }

    private List<Integer> calculateDisplayNum(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "표시할 번호가 없습니다.");
        }

        List<Integer> displayNum = new ArrayList<>();
        int size = numbers.size();
        for (int i = 0; i < 3; i++) {
            displayNum.add(i % size);
        }
        return displayNum;
    }

    @Transactional
    public AdminVisitStatusUpdateResponse updateVisitStatus(Long visitId) {
        if (visitId == null) {
            throw new GlobalException(ErrorCode.INVALID_VISIT_NUMBER);
        }

        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
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
                Visit previousVisit = visitRepository.findPreviousVisit(section.getId(), currentVisit.getNum())
                    .orElse(null);
                int previousNum = previousVisit != null ? previousVisit.getNum() : 0;
                String previousCategory = previousVisit != null ? previousVisit.getCategory().getCategory() : "";
                
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
                Optional<Visit> nextVisitOpt = pendingVisits.stream().findFirst();

                int nextNum = nextVisitOpt.map(Visit::getNum).orElse(0);
                String nextCategory = nextVisitOpt.map(visit -> visit.getCategory().getCategory()).orElse("");

                // 9. 웹소켓 메시지 전송
                String message = String.format(
                    "VISIT_UPDATE:%d",
                    section.getId()
                );
                websocketHandler.notifySubscribers(section.getBranch().getId(), message);

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

            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new GlobalException(ErrorCode.CONCURRENT_VISIT_UPDATE, "Visit ID: " + visitId);
                }
                try {
                    Thread.sleep(100 * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new GlobalException(ErrorCode.CONCURRENT_VISIT_UPDATE, "Visit ID: " + visitId);
                }
            }
        }
        
        throw new GlobalException(ErrorCode.CONCURRENT_VISIT_UPDATE, "Visit ID: " + visitId);
    }


    public AdminVisitStatusUpdateResponse getCurrentVisit(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_SECTION));

        Visit currentVisit = visitRepository.findCurrentProgressVisit(sectionId, Status.PROGRESS)
                .orElse(null);

        CsVisit csVisit = csVisitRepository.findByBranchIdAndDate(section.getBranch().getId(), LocalDate.now())
                .orElseGet(() -> {
                    CsVisit newCsVisit = CsVisit.builder()
                            .branch(section.getBranch())
                            .date(LocalDate.now())
                            .build();
                    return csVisitRepository.save(newCsVisit);
                });

        if (currentVisit == null) {
            Visit nextVisit = visitRepository.findNextPendingVisit(
                sectionId, 
                Status.PENDING
            ).orElse(null);

            log.info("Section ID: {}, Next Visit: {}", sectionId, 
                nextVisit != null ? String.format("num=%d, category=%s", nextVisit.getNum(), nextVisit.getCategory()) : "null");

            AdminVisitStatusUpdateResponse.SectionInfo sectionInfo = AdminVisitStatusUpdateResponse.SectionInfo.builder()
                    .sectionId(section.getId())
                    .sectionType(section.getSectionType().getType())
                    .currentNum(section.getCurrentNum())
                    .waitAmount(section.getWaitAmount())
                    .waitTime(section.getWaitTime())
                    .todayVisitors(csVisit.getTotalNum())
                    .build();

            return AdminVisitStatusUpdateResponse.builder()
                    .previousNum(0)
                    .previousCategory("")
                    .currentNum(0)
                    .currentCategory("")
                    .nextNum(nextVisit != null ? nextVisit.getNum() : 0)
                    .nextCategory(nextVisit != null ? nextVisit.getCategory().getCategory() : "")
                    .sectionInfo(sectionInfo)
                    .build();
        }

        Visit previousVisit = visitRepository.findPreviousVisit(sectionId, currentVisit.getNum())
                .orElse(null);

        Visit nextVisit = visitRepository.findNextPendingVisit(
            sectionId, 
            Status.PENDING
        ).orElse(null);

        log.info("Section ID: {}, Next Visit: {}", sectionId, 
            nextVisit != null ? String.format("num=%d, category=%s", nextVisit.getNum(), nextVisit.getCategory()) : "null");

        AdminVisitStatusUpdateResponse.SectionInfo sectionInfo = AdminVisitStatusUpdateResponse.SectionInfo.builder()
                .sectionId(section.getId())
                .sectionType(section.getSectionType().getType())
                .currentNum(section.getCurrentNum())
                .waitAmount(section.getWaitAmount())
                .waitTime(section.getWaitTime())
                .todayVisitors(csVisit.getTotalNum())
                .build();

        return AdminVisitStatusUpdateResponse.builder()
                .previousNum(previousVisit != null ? previousVisit.getNum() : 0)
                .previousCategory(previousVisit != null ? previousVisit.getCategory().getCategory() : "")
                .currentNum(currentVisit.getNum())
                .currentCategory(currentVisit.getCategory().getCategory())
                .nextNum(nextVisit != null ? nextVisit.getNum() : 0)
                .nextCategory(nextVisit != null ? nextVisit.getCategory().getCategory() : "")
                .sectionInfo(sectionInfo)
                .build();
    }
}
