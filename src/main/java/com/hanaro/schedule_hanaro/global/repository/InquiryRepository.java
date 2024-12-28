package com.hanaro.schedule_hanaro.global.repository;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryDto;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryDto;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

	// 번호 추가
	@Query("SELECT COALESCE(MAX(i.inquiryNum), 0) FROM Inquiry i")
	int findMaxInquiryNum();

	List<Inquiry> findAllByCustomerId(Long customerId);

	@Query(nativeQuery = true, value = """ 
    SELECT 
        CAST(COUNT(CASE WHEN DATE(i.created_at) = CURRENT_DATE THEN 1 END) AS SIGNED) as today, 
        CAST(COUNT(CASE WHEN i.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY) THEN 1 END) AS SIGNED) as weekly, 
        CAST(COUNT(CASE WHEN i.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN 1 END) AS SIGNED) as monthly, 
        CAST(COUNT(*) AS SIGNED) as total 
    FROM Inquiry i 
    WHERE i.admin_id = :adminId
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

	@Query("SELECT i FROM Inquiry i " +
		"LEFT JOIN Customer c ON i.customer.id = c.id " +
		"WHERE (i.admin.id = :adminId)" +
		"AND (:status IS NULL OR i.inquiryStatus = :status) " +
		"AND (:category IS NULL OR i.category = :category) " +
		"AND (:searchContent IS NULL OR " +
		"     i.tags LIKE %:searchContent% OR " +
		"     i.content LIKE %:searchContent% OR " +
		"     c.name LIKE %:searchContent%) " +
		"ORDER BY i.createdAt DESC")
	Page<Inquiry> findFilteredInquiries(
		@Param("adminId") Long adminId,
		@Param("status") InquiryStatus status,
		@Param("category") Category category,
		@Param("searchContent") String searchContent,
		Pageable pageable
	);

	@Query("SELECT i FROM Inquiry i LEFT JOIN InquiryResponse r ON i.id = r.inquiry.id LEFT JOIN Customer c ON i.customer.id = c.id WHERE i.id = :inquiryId")
	Optional<Inquiry> findInquiryDetailById(@Param("inquiryId") Long inquiryId);

	Integer countInquiryByCustomer(Customer customer);

	Slice<Inquiry> findByCustomerIdAndInquiryStatus(Long customerId, InquiryStatus inquiryStatus, Pageable pageable);

	@Modifying
	@Query("UPDATE Inquiry i SET i.inquiryStatus = 'REGISTRATIONCOMPLETE' WHERE i.id = :inquiryId")
	void changeStatusById(Long inquiryId);

	int countByAdminIdAndInquiryStatus(Long adminId, InquiryStatus status);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Inquiry> findFirstByOrderByIdDesc();
}
