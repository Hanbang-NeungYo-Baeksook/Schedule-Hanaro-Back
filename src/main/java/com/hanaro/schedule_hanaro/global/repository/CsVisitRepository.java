package com.hanaro.schedule_hanaro.global.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.CsVisit;

import jakarta.persistence.LockModeType;

@Repository
public interface CsVisitRepository extends JpaRepository<CsVisit, Long> {
	Optional<CsVisit> findById(final Long id);

	@Query("SELECT cv FROM CsVisit cv " +
		   "WHERE cv.branch.id = :branchId " +
		   "AND cv.date = :date")
	Optional<CsVisit> findByBranchIdAndDate(@Param("branchId") Long branchId, @Param("date") LocalDate date);

	Optional<CsVisit> findByBranchId(Long id);

	Optional<CsVisit> findCsVisitByBranchIdAndDate(Long branchId, LocalDate date);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT c FROM CsVisit c WHERE c.branch.id = :branchId AND c.date = :date")
	Optional<CsVisit> findByBranchIdAndDateWithPessimisticLock(@Param("branchId") Long branchId, @Param("date") LocalDate date);

	// Native Query로도 시도해볼 수 있습니다
	@Query(value = "SELECT * FROM cs_visit " +
		   "WHERE branch_id = :branchId " +
		   "AND date = :date", 
		   nativeQuery = true)
	Optional<CsVisit> findByBranchIdAndDateNative(@Param("branchId") Long branchId, @Param("date") LocalDate date);
}
