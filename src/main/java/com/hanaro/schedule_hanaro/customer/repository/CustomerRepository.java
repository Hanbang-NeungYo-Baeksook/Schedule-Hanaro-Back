package com.hanaro.schedule_hanaro.customer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanaro.schedule_hanaro.global.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	@Override
	Optional<Customer> findById(Long Long);

}
