package com.hanaro.schedule_hanaro.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanaro.schedule_hanaro.global.domain.CallMemo;

public interface CallMemoRepository extends JpaRepository<CallMemo, Long> {
}
