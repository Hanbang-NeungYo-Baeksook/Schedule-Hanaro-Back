package com.hanaro.schedule_hanaro.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

public interface CallRepository extends JpaRepository<Call, Long> {

	List<Call> findByStatus(Status status);

	@Modifying
	@Query("UPDATE Call c SET c.status = :status WHERE c.id = :callId")
	void updateStatus(Long callId, Status status);

	@Query("SELECT c FROM Call c WHERE c.customer.id = :customerId AND c.id != :callId AND c.status = 'COMPLETE'")
	List<Call> findCallHistoryByCustomerId(Long customerId, Long callId);
}
