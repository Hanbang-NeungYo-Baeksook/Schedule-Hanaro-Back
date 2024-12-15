package com.hanaro.schedule_hanaro.admin.repository;

import com.hanaro.schedule_hanaro.global.domain.InquiryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminInquiryResponseRepository extends JpaRepository<InquiryResponse, Long> {
	@Query("SELECT ir FROM InquiryResponse ir WHERE ir.inquiry.id = :inquiryId")
	Optional<InquiryResponse> findByInquiryId(@Param("inquiryId") Long inquiryId);
}
