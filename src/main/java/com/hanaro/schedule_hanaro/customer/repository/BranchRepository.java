package com.hanaro.schedule_hanaro.customer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hanaro.schedule_hanaro.global.domain.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {
	@Override
	Optional<Branch> findById(Long aLong);
}
