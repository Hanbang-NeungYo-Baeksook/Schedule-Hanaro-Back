package com.hanaro.schedule_hanaro.customer.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaro.schedule_hanaro.customer.dto.response.CustomerInfoResponse;
import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Gender;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CustomerRepository customerRepository;

	@BeforeAll
	public void beforeAll() {
		for (int i = 1; i <= 2; i++) {
			String authId = "TestAuthId" + i;
			String passwd = "TestPasswd" + i;
			String name = "TestName" + i;
			String phone = "0100000000" + i;
			LocalDate brith = LocalDate.of(2002, 4, 15);
			Customer customer = new Customer(
				authId, passwd, name, phone, brith, Gender.FEMALE
			);
			customerRepository.save(customer);
		}
	}

	@AfterAll
	public void AfterAll() {
		Customer testCustomer1 = customerRepository.findByAuthId("TestAuthId1").orElseThrow();
		customerRepository.delete(testCustomer1);
		Customer testCustomer2 = customerRepository.findByAuthId("TestAuthId2").orElseThrow();
		customerRepository.delete(testCustomer2);
	}

	@Test
	@Order(1)
	public void getCustomerListTest() throws Exception {
		Customer testCustomer = customerRepository.findByAuthId("TestAuthId1").orElseThrow();
		String url = "/api/customers/" + testCustomer.getId();

		String reqStr = objectMapper.writeValueAsString(
			CustomerInfoResponse.from(testCustomer.getName(), testCustomer.getPhoneNum()));

		ResultActions result = mockMvc.perform(get(url));
		result.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(content().json(reqStr));
	}
}
