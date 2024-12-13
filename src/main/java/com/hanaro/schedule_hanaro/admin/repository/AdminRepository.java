package com.hanaro.schedule_hanaro.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.global.domain.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	@Override
	Optional<Admin> findById(Long id);
}
