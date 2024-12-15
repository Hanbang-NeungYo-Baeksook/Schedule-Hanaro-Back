package com.hanaro.schedule_hanaro.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInfoResponse;
import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;

@Service
public class AdminCustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public AdminCustomerInfoResponse findCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        return AdminCustomerInfoResponse.from(customer);
    }

}
