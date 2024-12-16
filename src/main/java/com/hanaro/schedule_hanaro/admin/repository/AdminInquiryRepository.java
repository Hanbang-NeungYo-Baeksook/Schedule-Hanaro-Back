package com.hanaro.schedule_hanaro.admin.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.InquiryResponse;

public interface AdminInquiryRepository extends JpaRepository<Inquiry, Long> {
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
