package com.hanaro.schedule_hanaro.global.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;


public interface CallRepository extends JpaRepository<Call, Long> {

	@Query("SELECT COUNT(c) FROM Call c WHERE DATE(c.callDate) = :date AND c.callDate BETWEEN :startTime AND :endTime")
	int countByDateAndTimeSlot(@Param("date") LocalDate date, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

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

	@Query(nativeQuery = true, value = """ 
		SELECT 
			CAST(COUNT(CASE WHEN DATE(c.call_date) = CURRENT_DATE THEN 1 END) AS SIGNED) as today, 
			CAST(COUNT(CASE WHEN c.call_date >= DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY) THEN 1 END) AS SIGNED) as weekly, 
			CAST(COUNT(CASE WHEN c.call_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN 1 END) AS SIGNED) as monthly, 
			CAST(COUNT(*) AS SIGNED) as total 
		FROM `Call` c
		JOIN `Call_Memo` cm ON c.call_id = cm.call_id 
		WHERE cm.admin_id = :adminId 
		AND c.status = 'COMPLETE'
	""")
	List<Object[]> findStatsByAdminId(@Param("adminId") Long adminId);

	default AdminInquiryStatsDto getStatsByAdminId(Long adminId) {
		Object[] result = findStatsByAdminId(adminId).get(0);
		return AdminInquiryStatsDto.of(
			((Number) result[0]).intValue(),
			((Number) result[1]).intValue(),
			((Number) result[2]).intValue(),
			((Number) result[3]).intValue()
		);
	}

}