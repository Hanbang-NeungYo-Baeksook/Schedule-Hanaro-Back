package com.hanaro.schedule_hanaro.customer.repository;

import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
import com.hanaro.schedule_hanaro.global.domain.Inquiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
