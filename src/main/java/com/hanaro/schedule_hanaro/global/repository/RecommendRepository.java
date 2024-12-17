package com.hanaro.schedule_hanaro.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanaro.schedule_hanaro.global.domain.Recommend;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {

}
