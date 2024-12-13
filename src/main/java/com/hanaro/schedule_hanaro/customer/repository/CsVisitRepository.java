package com.hanaro.schedule_hanaro.customer.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.global.domain.CsVisit;

@Repository
public interface CsVisitRepository extends JpaRepository<CsVisit, Integer> {

	CsVisit findCsVisitByBranch_IdAndDate(Long branchId, LocalDate date);
}
