package com.hanaro.schedule_hanaro.global.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;

import jakarta.persistence.LockModeType;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

	@Override
	Optional<Section> findById(Long aLong);

	@Lock(value = LockModeType.OPTIMISTIC)
	@Query("select s from Section s where s.id = :id")
	Optional<Section> findByIdWithOptimisticLock(final Long id);

	Optional<Section> findByBranchAndSectionType(Branch branch, SectionType sectionType);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Section s WHERE s.id = :id")
	Optional<Section> findByIdWithPessimisticLock(@Param("id") Long id);
	void deleteAllByBranch(Branch branch);
}
