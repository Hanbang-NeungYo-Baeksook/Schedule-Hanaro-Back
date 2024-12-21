package com.hanaro.schedule_hanaro.global.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import com.hanaro.schedule_hanaro.customer.dto.response.BankInfoDto;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;

public interface BranchRepository extends JpaRepository<Branch, Long> {
	@Override
	Optional<Branch> findById(Long id);

	List<Branch> findAllByBranchTypeOrderByIdAsc(BranchType branchType);

	Optional<Branch> findByName(String name);

	List<Branch> findAllByBranchType(BranchType branchType);

	@Query("""
select new com.hanaro.schedule_hanaro.customer.dto.response.BankInfoDto(
b.id, b.name,b.xPosition,b.yPosition,b.address,'영업점',
s1.waitAmount,s1.waitTime,s2.waitAmount,s2.waitTime,s3.waitAmount,s3.waitTime
)
from Branch b
join Section s1 on s1.branch.id=b.id and s1.sectionType=:section1
join Section s2 on s2.branch.id=b.id and s2.sectionType=:section2
join Section s3 on s3.branch.id=b.id and s3.sectionType=:section3
where b.branchType=:branchType
order by b.id asc
""")
	List<BankInfoDto> findBankInfoDtoByBranchTypeAndSectionTypes(
		BranchType branchType,
		SectionType section1,
		SectionType section2,
		SectionType section3);

}
