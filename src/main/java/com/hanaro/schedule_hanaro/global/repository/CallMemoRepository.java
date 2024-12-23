package com.hanaro.schedule_hanaro.global.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanaro.schedule_hanaro.global.domain.CallMemo;

public interface CallMemoRepository extends JpaRepository<CallMemo, Long> {
	@Query("SELECT cm FROM CallMemo cm WHERE cm.call.id = :callId")
	CallMemo findByCallId(@Param("callId") Long callId);
}
