package com.hanaro.schedule_hanaro.customer.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.global.domain.CsVisit;

@Repository
public interface CsVisitRepository extends JpaRepository<CsVisit, Integer> {

	Optional<CsVisit> findCsVisitByBranchIdAndDate(Long branchId, LocalDate date);

	List<CsVisit> findAllByDateOrderByBranchAsc(LocalDate date);

}
