package com.hanaro.schedule_hanaro.branch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.branch.entity.Branch;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

	Branch findById(long branchId);
}
