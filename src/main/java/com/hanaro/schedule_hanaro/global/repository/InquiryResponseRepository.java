package com.hanaro.schedule_hanaro.global.repository;

import com.hanaro.schedule_hanaro.global.domain.Inquiry;
import com.hanaro.schedule_hanaro.global.domain.InquiryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

public interface InquiryResponseRepository extends JpaRepository<InquiryResponse, Long> {
	// @Query("SELECT ir FROM InquiryResponse ir WHERE ir.inquiry.id = :inquiryId")
	Optional<InquiryResponse> findByInquiryId(@Param("inquiryId") Long inquiryId);
	Optional<InquiryResponse> findByInquiry(Inquiry inquiry);

}
