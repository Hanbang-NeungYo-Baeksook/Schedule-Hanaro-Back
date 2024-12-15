package com.hanaro.schedule_hanaro.customer.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Visit;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

public interface VisitRepository extends JpaRepository<Visit, Long> {
	Boolean existsByCustomerAndBranchAndVisitDateAndStatus(
		Customer customer,
		Branch branch,
		LocalDate localDate,
		Status status
	);

	int countByCustomerAndVisitDateAndStatus(
		Customer customer,
		LocalDate localDate,
		Status status
	);

	List<Visit> findAllByBranchIdAndNumLessThanAndStatus(Long branchId, int num, Status status);

	Optional<Visit> findByBranchId(Long id);

	List<Visit> findAllByBranchId(Long id);
}
