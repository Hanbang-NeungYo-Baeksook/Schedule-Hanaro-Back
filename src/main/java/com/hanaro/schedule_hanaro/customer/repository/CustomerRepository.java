package com.hanaro.schedule_hanaro.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanaro.schedule_hanaro.customer.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Customer findById(long id);
}
