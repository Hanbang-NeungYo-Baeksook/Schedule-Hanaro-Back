package com.hanaro.schedule_hanaro.global.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hanaro.schedule_hanaro.customer.vo.BankVO;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;

public interface BranchRepository extends JpaRepository<Branch, Long> {
	@Override
	Optional<Branch> findById(Long id);

	List<Branch> findAllByBranchTypeOrderByIdAsc(BranchType branchType);

	Optional<Branch> findByName(String name);

	List<Branch> findAllByBranchType(BranchType branchType);

	@Query("""
		select new com.hanaro.schedule_hanaro.customer.vo.BankVO(
		b.id, b.name,b.xPosition,b.yPosition,b.address,
		b.branchType,s.sectionType, s.waitTime,s.waitAmount)
		from Branch b
		left join Section s on s.branch.id=b.id
		where b.branchType=:branchType
		order by b.id asc, s.sectionType asc
		""")
	List<BankVO> findBranchByBranchType(BranchType branchType);

	@Query("""
		select new com.hanaro.schedule_hanaro.customer.vo.BankVO(
		b.id, b.name,b.xPosition,b.yPosition,b.address,
		b.branchType,s.sectionType, s.waitTime,s.waitAmount)
		from Branch b
		left join Section s on s.branch.id=b.id
		where b.id=:branchId
		order by s.sectionType asc
		""")
	List<BankVO> findBranchByBranch_Id(Long branchId);
}
