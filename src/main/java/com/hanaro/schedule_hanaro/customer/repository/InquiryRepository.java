package com.hanaro.schedule_hanaro.customer.repository;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryDto;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

	@Query("SELECT i FROM Inquiry i WHERE i.inquiryStatus = :status ORDER BY i.createdAt DESC")
	List<Inquiry> findByStatus(Status status);

	@Query("SELECT COUNT(i) FROM Inquiry i WHERE i.inquiryStatus = :status")
	int countByStatus(Status status);

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
}
