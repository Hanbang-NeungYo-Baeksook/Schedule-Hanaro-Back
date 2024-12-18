package com.hanaro.schedule_hanaro.global.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.Visit;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

public interface VisitRepository extends JpaRepository<Visit, Long> {
	Boolean existsByCustomerAndSectionAndVisitDateAndStatus(
		Customer customer,
		Section section,
		LocalDate localDate,
		Status status
	);

	int countByCustomerAndVisitDateAndStatus(
		Customer customer,
		LocalDate localDate,
		Status status
	);

	List<Visit> findAllBySectionIdAndNumLessThanAndStatus(Long sectionId, int num, Status status);

	List<Visit> findAllBySection_Id(Long id);

	Slice<Visit> findByCustomerIdAndStatus(Long customerId, Status status, Pageable pageable);
}
