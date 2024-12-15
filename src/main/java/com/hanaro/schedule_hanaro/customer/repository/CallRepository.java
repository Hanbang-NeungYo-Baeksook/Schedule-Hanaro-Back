package com.hanaro.schedule_hanaro.customer.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.hanaro.schedule_hanaro.global.domain.Call;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

public interface CallRepository extends JpaRepository<Call, Long> {

	@Query("SELECT COALESCE(MAX(c.callNum), 0) FROM Call c WHERE c.callDate = :callDate")
	int findMaxCallNumByDate(@Param("callDate") LocalDateTime callDate);

	boolean existsByCallDate(LocalDateTime callDate);

	Slice<Call> findByStatus(Status status, Pageable pageable);
}
