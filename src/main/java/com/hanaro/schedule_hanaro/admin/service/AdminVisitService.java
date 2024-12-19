package com.hanaro.schedule_hanaro.admin.service;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitCarouselDto;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitNumResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitStatisticsDto;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitStatusUpdateResponse;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import com.hanaro.schedule_hanaro.global.repository.CsVisitRepository;
import com.hanaro.schedule_hanaro.global.repository.SectionRepository;
import com.hanaro.schedule_hanaro.global.repository.VisitRepository;
import com.hanaro.schedule_hanaro.global.domain.CsVisit;
import com.hanaro.schedule_hanaro.global.domain.Visit;
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

    public Visit findVisitById(Long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow();
    }

    private final CsVisitRepository csVisitRepository;

    public AdminVisitNumResponse getVisitPageData(Long visitId) {
        CsVisit visit = csVisitRepository.findById(visitId)
                .orElseThrow();

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
        List<Integer> displayNum = new ArrayList<>();
        int size = numbers.size();
        for (int i = 0; i < 3; i++) {
            displayNum.add(i % size);
        }
        return displayNum;
    }

    @Transactional
    public AdminVisitStatusUpdateResponse updateVisitStatus(Long visitId) {
        // Visit 조회 및 상태 변경
        Visit currentVisit = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("해당 방문 정보가 존재하지 않습니다. ID: " + visitId));

        if (currentVisit.getStatus() != Status.PENDING) {
            throw new IllegalStateException("해당 방문은 이미 진행 중입니다.");
        }

        int previousNum = currentVisit.getNum();
        currentVisit.changeStatusToProgress();

        //Section 조회 및 현재 상태 업데이트
        Section section = currentVisit.getSection();
        section.StatusUpdatePendingToProgress(currentVisit,10);
        sectionRepository.save(section);

        //다음 대기 번호 설정
        Visit nextVisit = visitRepository.findNextPendingVisit(section.getId(), Status.PENDING)
                .orElse(null);

        int nextNum = (nextVisit != null) ? nextVisit.getNum() : -1;

        //CsVisit 업데이트
        CsVisit csVisit = csVisitRepository.findByBranchIdAndDate(section.getBranch().getId(), LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("해당 CS 방문 통계가 존재하지 않습니다."));

        csVisit.increaseTotalNum();
        csVisitRepository.save(csVisit);

        //Response 반환
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
                .currentNum(currentVisit.getNum())
                .nextNum(nextNum)
                .sectionInfo(sectionInfo)
                .build();
    }
}