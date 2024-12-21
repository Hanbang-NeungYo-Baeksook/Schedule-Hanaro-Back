package com.hanaro.schedule_hanaro.global.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
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
}
