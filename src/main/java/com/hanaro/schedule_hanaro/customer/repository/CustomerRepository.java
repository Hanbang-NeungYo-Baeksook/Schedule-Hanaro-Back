package com.hanaro.schedule_hanaro.customer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hanaro.schedule_hanaro.global.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Optional<Customer> findByAuthId(String authId);

	Optional<Customer> findFirstBy();

	Optional<Customer> findByName(String name);
}
