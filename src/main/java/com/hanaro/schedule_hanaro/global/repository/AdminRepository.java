package com.hanaro.schedule_hanaro.global.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanaro.schedule_hanaro.global.domain.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
	@Override
	Optional<Admin> findById(Long aLong);

	Optional<Admin> findByAuthId(String authId);

	Optional<Admin> findFirstByIdGreaterThanOrderByIdAsc(Long currentId);


	Optional<Admin> findFirstByOrderByIdAsc();

}
