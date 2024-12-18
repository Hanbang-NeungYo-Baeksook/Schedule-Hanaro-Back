package com.hanaro.schedule_hanaro.global.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

	@Override
	Optional<Section> findById(Long aLong);

	Optional<Section> findByBranchAndSectionType(Branch branch, SectionType sectionType);
}
