package com.hanaro.schedule_hanaro.global.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import jakarta.persistence.LockModeType;

public interface CallRepository extends JpaRepository<Call, Long> {

	@Query("SELECT COUNT(c) FROM Call c WHERE c.callDate BETWEEN :startTime AND :endTime AND c.status = :status")
	int countByTimeSlotAndStatus(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("status") Status status);

	Slice<Call> findByStatus(Status status, Pageable pageable);

	List<Call> findByStatus(Status status);

	@Modifying
	@Query("UPDATE Call c SET c.status = :status WHERE c.id = :callId")
	void updateStatus(Long callId, Status status);

	@Query("SELECT c FROM Call c WHERE c.customer.id = :customerId AND c.id != :callId AND c.status = 'COMPLETE'")
	List<Call> findCallHistoryByCustomerId(Long customerId, Long callId);

	@Query("SELECT c FROM Call c " +
		"WHERE c.status = :status " +
		"AND (:category IS NULL OR c.category = :category) " +
		"AND (:keyword IS NULL OR c.content LIKE %:keyword%) " +
		"AND (:startedAt IS NULL OR c.startedAt >= :startedAt) " +
		"AND (:endedAt IS NULL OR c.endedAt <= :endedAt)")
	Slice<Call> findByFiltering(
		Pageable pageable,
		Status status,
		@Param("startedAt") LocalDate startedAt,
		@Param("endedAt") LocalDate endedAt,
		@Param("category") Category category,
		@Param("keyword") String keyword
	);

	List<Call> findByCustomerId(Long customerId);

}
