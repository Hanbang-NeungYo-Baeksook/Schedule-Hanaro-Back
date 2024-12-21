package com.hanaro.schedule_hanaro.global.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.customer.dto.response.BankInfoDto;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;

import jakarta.persistence.LockModeType;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

	@Override
	Optional<Section> findById(Long aLong);

	List<Section> findSectionsByBranchId(Long branchId);

	@Lock(value = LockModeType.OPTIMISTIC)
	@Query("select s from Section s where s.id = :id")
	Optional<Section> findByIdWithOptimisticLock(final Long id);

	Optional<Section> findByBranchAndSectionType(Branch branch, SectionType sectionType);

	List<Section>findByBranch_IdOrderBySectionTypeAsc(Long branchId);

	@Query("select new com.hanaro.schedule_hanaro.customer.dto.response.BankInfoDto(b.id, b.name, b.xPosition,b.yPosition,b.address,b.branchType,s.waitAmount,s.waitTime) from Section s join s.branch b where s.branch=:branch")
	List<BankInfoDto>findSectionsByBranch(Branch branch);

	List <Section> findAllOrderByBranch_IdAscAndSectionTypeAsc();
}
