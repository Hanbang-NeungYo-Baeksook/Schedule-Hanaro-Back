package com.hanaro.schedule_hanaro.admin.service;

import com.hanaro.schedule_hanaro.customer.repository.CsVisitRepository;
import com.hanaro.schedule_hanaro.global.domain.Visit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminVisitService {
    private final VisitRepository visitRepository;

    public Visit findVisitById(Long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow();
    }
}

