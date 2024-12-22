package com.hanaro.schedule_hanaro.global.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.Visit;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VisitRepository extends JpaRepository<Visit, Long> {

	@Override
	Optional<Visit> findById(Long aLong);

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

	List<Visit> findByCustomer_IdAndStatus(Long customerId, Status status);

	List<Visit> findAllBySection_Id(Long id);

	Slice<Visit> findByCustomerIdAndStatus(Long customerId, Status status, Pageable pageable);

	@Query("SELECT v FROM Visit v WHERE v.section.id = :sectionId AND v.status = :status ORDER BY v.num ASC")
	Optional<Visit> findNextPendingVisit(@Param("sectionId") Long sectionId, @Param("status") Status status);

	@Query("select v.category from Visit v where v.section.id = :sectionId and v.status = :status and v.num < :numBefore")
	List<Category> findCategoryBySectionIdAndNumBeforeAndStatus(Long sectionId, int numBefore, Status status);

	@Query("SELECT v FROM Visit v WHERE v.section.id = :sectionId AND v.status = :status")
	Optional<Visit> findCurrentProgressVisit(@Param("sectionId") Long sectionId, @Param("status") Status status);
}
