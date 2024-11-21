package com.hanaro.schedule_hanaro.branch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.branch.entity.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {

	Branch findById(long id);

	@Query("select b from Branch b where b.branchNum = :branchNum")
	Branch findBranchByBranchNum(long branchNum);

}
