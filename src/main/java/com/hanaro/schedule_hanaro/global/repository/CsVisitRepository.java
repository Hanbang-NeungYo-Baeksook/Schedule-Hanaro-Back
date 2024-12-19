package com.hanaro.schedule_hanaro.global.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.CsVisit;

import jakarta.persistence.LockModeType;

@Repository
public interface CsVisitRepository extends JpaRepository<CsVisit, Long> {

	Optional<CsVisit> findByBranchIdAndDate(Long branchId, LocalDate date);

	@Lock(value = LockModeType.OPTIMISTIC)
	@Query("select c from CsVisit c where c.id = :id")
	Optional<CsVisit> findByWithOptimisticLock(final Long id);

	@Lock(value = LockModeType.OPTIMISTIC)
	@Query("select c from CsVisit c where c.branch = :branch and c.date = :date")
	Optional<CsVisit> findByBranchAndDateWithOptimisticLock(Branch branch, LocalDate date);

	Optional<CsVisit> findByBranchId(Long id);

	Optional<CsVisit> findCsVisitByBranchIdAndDate(Long branchId, LocalDate date);

	List<CsVisit> findAllByDateOrderByBranchAsc(LocalDate date);

}
