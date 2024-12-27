package com.hanaro.schedule_hanaro.global.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import jakarta.persistence.LockModeType;

public interface CallRepository extends JpaRepository<Call, Long> {

	@Query(value = "SELECT MAX(call_num) FROM `Call` WHERE call_date BETWEEN :startTime AND :endTime FOR UPDATE", nativeQuery = true)
	Integer findMaxCallNumByCallDateBetweenForUpdate(@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime);

	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
		"FROM Call c " +
		"WHERE c.customer.id = :customerId " +
		"AND c.callDate BETWEEN :startTime AND :endTime " +
		"AND c.status <> 'CANCELED'")
	boolean isExistReservationsInSlot(@Param("customerId") Long customerId,
		@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime);

	@Query("SELECT COUNT(c) FROM Call c WHERE c.callDate BETWEEN :startTime AND :endTime")
	int countByCallDateBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
	Call findByCallNum(int callNum);

	Slice<Call> findByCustomerIdAndStatus(Long customerId, Status status, Pageable pageable);

	@Modifying
	@Query("UPDATE Call c SET c.status = :status, c.endedAt = :endedAt WHERE c.id = :callId")
	void updateStatusWithEndedAt(@Param("callId") Long callId, @Param("status") Status status, @Param("endedAt") LocalDateTime endedAt);

	@Modifying
	@Query("UPDATE Call c SET c.status = :status, c.startedAt = :startedAt WHERE c.id = :callId")
	void updateStatusWithStartedAt(@Param("callId") Long callId, @Param("status") Status status, @Param("startedAt") LocalDateTime startedAt);


	@Query("SELECT c FROM Call c " +
		"WHERE c.status = :status " +
		"AND (:category IS NULL OR c.category = :category) " +
		"AND (:keyword IS NULL OR c.content LIKE %:keyword%) " +
		"AND (:startedAt IS NULL OR c.startedAt >= :startedAt) " +
		"AND (:endedAt IS NULL OR c.endedAt <= :endedAt)")
	Slice<Call> findByFiltering(
		Pageable pageable,
		Status status,
		@Param("startedAt") LocalDateTime startedAt,
		@Param("endedAt") LocalDateTime endedAt,
		@Param("category") Category category,
		@Param("keyword") String keyword
	);

	List<Call> findByCustomerId(Long customerId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Call> findFirstByStatusOrderByCallNumAsc(Status status);

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

	Integer countCallsByCustomer(Customer customer);

	@Query("SELECT c " +
		"FROM Call c " +
		"JOIN CallMemo cm ON cm.call.id = c.id " +
		"WHERE cm.admin.id = :adminId " +
		"AND c.status = :status ")
	List<Call> findByStatusAndAdminId(
		@Param("status") Status status,
		@Param("adminId") Long adminId
	);


	@Query("SELECT c FROM Call c " +
		"WHERE c.status = 'PENDING' " +
		"AND (:startDateTime IS NULL OR c.callDate >= :startDateTime) " +
		"AND (:endDateTime IS NULL OR c.callDate < :endDateTime)")
	List<Call> findPendingCallsByDateTimeRange(
		@Param("startDateTime") LocalDateTime startDateTime,
		@Param("endDateTime") LocalDateTime endDateTime
	);


}
