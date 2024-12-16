package com.hanaro.schedule_hanaro.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.global.domain.Customer;

@Repository
public interface AdminCustomerRepository extends JpaRepository<Customer, Long> {
	// Page<Customer> findAll(Pageable pageable);
}
