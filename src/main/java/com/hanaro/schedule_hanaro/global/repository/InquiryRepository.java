package com.hanaro.schedule_hanaro.global.repository;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryDto;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryDto;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;

import java.util.List;
import java.util.Optional;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

	// 번호 추가
	@Query("SELECT COALESCE(MAX(i.inquiryNum), 0) FROM Inquiry i")
	int findMaxInquiryNum();

	Page<Inquiry> findByInquiryStatus(InquiryStatus status, Pageable pageable);

	@Query("SELECT i FROM Inquiry i WHERE i.customer.id = :customerId")
	List<Inquiry> findByCustomerId(Long customerId);

	List<Inquiry> findAllByCustomerId(Long customerId);

	@Query(value = """ 
    SELECT 
        COUNT(CASE WHEN i.created_at >= CURRENT_DATE THEN 1 END) AS today, 
        COUNT(CASE WHEN i.created_at >= CURRENT_DATE - INTERVAL 7 DAY THEN 1 END) AS weekly, 
        COUNT(CASE WHEN i.created_at >= CURRENT_DATE - INTERVAL 30 DAY THEN 1 END) AS monthly, 
        COUNT(*) AS total 
    FROM Inquiry i
    JOIN Inquiry_Response ir ON i.inquiry_id = ir.inquiry_id 
    WHERE ir.admin_id = :adminId 
""", nativeQuery = true)
	AdminInquiryStatsDto findStatsByAdminId(@Param("adminId") Long adminId);

	@Query("SELECT i FROM Inquiry i " +
		"LEFT JOIN Customer c ON i.customer.id = c.id " +
		"WHERE (i.inquiryStatus = :status) " +
		"AND (i.category = :category) " +
		"AND (:searchContent IS NULL OR " +
		"     i.tags LIKE %:searchContent% OR " +
		"     i.content LIKE %:searchContent% OR " +
		"     c.name LIKE %:searchContent%)" +
		"ORDER BY i.createdAt DESC")
	Page<Inquiry> findFilteredInquiries(
		@Param("status") String status,
		@Param("category") String category,
		@Param("searchContent") String searchContent,
		Pageable pageable
	);

	@Query("SELECT i FROM Inquiry i LEFT JOIN InquiryResponse r ON i.id = r.inquiry.id LEFT JOIN Customer c ON i.customer.id = c.id WHERE i.id = :inquiryId")
	Optional<Inquiry> findInquiryDetailById(@Param("inquiryId") Long inquiryId);
}
