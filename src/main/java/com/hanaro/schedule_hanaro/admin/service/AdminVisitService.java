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

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

        Visit currentVisit = visitRepository.findByIdWithPessimisticLock(visitId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_VISIT));

        if (currentVisit.getStatus() != Status.PENDING) {
            throw new GlobalException(ErrorCode.ALREADY_PROGRESS);
        }

        Section section = sectionRepository.findByIdWithPessimisticLock(currentVisit.getSection().getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_SECTION));

        if (section.getBranch() == null) {
            throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH);
        }

        if (section.getBranch().getId() == null) {
            throw new GlobalException(ErrorCode.NOT_FOUND_BRANCH);
        }

        // CsVisit 조회 및 유효성 검사
        CsVisit csVisit = csVisitRepository.findByBranchIdAndDateWithPessimisticLock(section.getBranch().getId(), LocalDate.now())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CS_VISIT));

        try {
            String previousCategory = currentVisit.getCategory().getCategory();
            int previousNum = currentVisit.getNum();
            currentVisit.changeStatusToProgress();

            // 현재 상태 업데이트
            section.updateStatusPendingToProgress(currentVisit.getNum(), 10);
            sectionRepository.save(section);

            // 다음 대기 번호 설정
            Visit nextVisit = visitRepository.findNextPendingVisitWithPessimisticLock(section.getId(), Status.PENDING)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_NEXT_VISITOR));

            int nextNum = (nextVisit != null) ? nextVisit.getNum() : -1;
            String nextCategory = (nextVisit != null) ? nextVisit.getCategory().getCategory() : "";

            String message = "다음 방문자 대기: [다음 번호: " + nextNum + ", 다음 카테고리: " + nextCategory + "]";
            websocketHandler.notifySubscribers(section.getBranch().getId(), message);

            // CsVisit 업데이트
            csVisit.increaseTotalNum();
            csVisitRepository.save(csVisit);

            // Response 반환
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

        } catch (Exception e) {
            throw new GlobalException(ErrorCode.CONCURRENT_VISIT_UPDATE);
        }
    }

    public AdminVisitStatusUpdateResponse getCurrentVisit(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_SECTION));

        Visit currentVisit = visitRepository.findCurrentProgressVisit(sectionId, Status.PROGRESS)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_VISIT));

        Visit nextVisit = visitRepository.findNextPendingVisit(sectionId, Status.PENDING)
                .orElse(null);

        CsVisit csVisit = csVisitRepository.findByBranchIdAndDate(section.getBranch().getId(), LocalDate.now())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CS_VISIT));

        AdminVisitStatusUpdateResponse.SectionInfo sectionInfo = AdminVisitStatusUpdateResponse.SectionInfo.builder()
                .sectionId(section.getId())
                .sectionType(section.getSectionType().getType())
                .currentNum(section.getCurrentNum())
                .waitAmount(section.getWaitAmount())
                .waitTime(section.getWaitTime())
                .todayVisitors(csVisit.getTotalNum())
                .build();

        return AdminVisitStatusUpdateResponse.builder()
                .previousNum(-1)
                .previousCategory("")
                .currentNum(currentVisit.getNum())
                .currentCategory(currentVisit.getCategory().getCategory())
                .nextNum(nextVisit != null ? nextVisit.getNum() : -1)
                .nextCategory(nextVisit != null ? nextVisit.getCategory().getCategory() : "")
                .sectionInfo(sectionInfo)
                .build();
    }
}
