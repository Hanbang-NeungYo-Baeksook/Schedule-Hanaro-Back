package com.hanaro.schedule_hanaro.customer.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Optional;

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
import com.hanaro.schedule_hanaro.customer.dto.request.VisitCreateRequest;
import com.hanaro.schedule_hanaro.customer.repository.BranchRepository;
import com.hanaro.schedule_hanaro.customer.repository.CsVisitRepository;
import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.customer.repository.VisitRepository;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.CsVisit;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Visit;
import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;
import com.hanaro.schedule_hanaro.global.domain.enums.Gender;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VisitControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BranchRepository branchRepository;

	@Autowired
	CsVisitRepository csVisitRepository;

	@Autowired
	VisitRepository visitRepository;

	@BeforeAll
	public void beforeAll() {
		Customer addTest = Customer
			.builder()
			.authId("TestAuthId")
			.name("TestUser")
			.password("TestPassword")
			.phoneNum("01012341234")
			.birth(LocalDate.of(2002, 4, 15))
			.gender(Gender.FEMALE)
			.build();
		customerRepository.save(addTest);

		for (int i = 1; i <= 4; i++) {
			Branch normalBranch = Branch
				.builder()
				.address("TestAddress" + i)
				.name("NormalTestBranch" + i)
				.businessTime("00:00~23:59")
				.tel("021231234")
				.xPosition("12.1234")
				.yPosition("21.1234")
				.branchType(BranchType.BANK)
				.build();
			branchRepository.save(normalBranch);
		}

		Branch abnormalBranch = Branch
			.builder()
			.address("TestAddress")
			.name("AbnormalTestBranch")
			.businessTime("09:00~09:01")
			.tel("021231234")
			.xPosition("12.1234")
			.yPosition("21.1234")
			.branchType(BranchType.BANK)
			.build();
		branchRepository.save(abnormalBranch);

		for (int i = 1; i <= 4; i++) {
			CsVisit normalCsVisit = CsVisit
				.builder()
				.currentNum(0)
				.date(LocalDate.now())
				.totalNum(0)
				.waitAmount(0)
				.branch(branchRepository.findByName("NormalTestBranch" + i).orElseThrow())
				.build();
			csVisitRepository.save(normalCsVisit);
		}

		CsVisit abnormalCsVisit = CsVisit
			.builder()
			.currentNum(0)
			.date(LocalDate.now())
			.totalNum(0)
			.waitAmount(0)
			.branch(abnormalBranch)
			.build();
		csVisitRepository.save(abnormalCsVisit);
	}

	@AfterAll
	public void afterAll() {
		for (int i = 1; i <= 4; i++) {
			Branch branch = branchRepository.findByName("NormalTestBranch" + i).orElseThrow();
			CsVisit csVisit = csVisitRepository.findByBranchId(branch.getId()).orElseThrow();
			Optional<Visit> byBranchId = visitRepository.findByBranchId(branch.getId());
			csVisitRepository.delete(csVisit);
			byBranchId.ifPresent(visit -> visitRepository.delete(visit));
			branchRepository.delete(branch);
		}
		Branch branch = branchRepository.findByName("AbnormalTestBranch").orElseThrow();
		CsVisit csVisit = csVisitRepository.findByBranchId(branch.getId()).orElseThrow();
		Optional<Visit> byBranchId = visitRepository.findByBranchId(branch.getId());
		csVisitRepository.delete(csVisit);
		byBranchId.ifPresent(visit -> visitRepository.delete(visit));
		branchRepository.delete(branch);

		Customer customer = customerRepository.findByAuthId("TestAuthId").orElseThrow();
		customerRepository.delete(customer);
	}

	@Test
	@Order(1)
	public void addVisitClosedErrorTest() throws Exception {
		Customer customer = customerRepository.findByName("TestUser").orElseThrow();
		Branch abnormalBranch = branchRepository.findByName("AbnormalTestBranch").orElseThrow();

		String url = "/api/visits";

		VisitCreateRequest abnormalVisitCreateRequest = VisitCreateRequest
			.builder()
			.customerId(customer.getId())
			.branchId(abnormalBranch.getId())
			.content("TestContent")
			.build();
		String reqStr = objectMapper.writeValueAsString(abnormalVisitCreateRequest);

		ResultActions result = mockMvc.perform(post(url).content(reqStr).contentType(MediaType.APPLICATION_JSON));

		result.andExpect(status().is5xxServerError())
			.andExpect(content().string("Branch Closed"));
	}

	@Test
	@Order(2)
	public void addVisitAndDuplicateErrorTest() throws Exception {
		Customer customer = customerRepository.findByName("TestUser").orElseThrow();
		Branch normalBranch = branchRepository.findByName("NormalTestBranch1").orElseThrow();

		String url = "/api/visits";

		VisitCreateRequest normalVisitCreateRequest = VisitCreateRequest
			.builder()
			.customerId(customer.getId())
			.branchId(normalBranch.getId())
			.content("TestContent")
			.build();
		String reqStr = objectMapper.writeValueAsString(normalVisitCreateRequest);

		ResultActions result = mockMvc.perform(post(url).content(reqStr).contentType(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());

		VisitCreateRequest abnormalVisitCreateRequest = VisitCreateRequest
			.builder()
			.customerId(customer.getId())
			.branchId(normalBranch.getId())
			.content("TestContent")
			.build();
		String reqStr2 = objectMapper.writeValueAsString(abnormalVisitCreateRequest);

		ResultActions result2 = mockMvc.perform(post(url).content(reqStr2).contentType(MediaType.APPLICATION_JSON));

		result2.andExpect(status().is5xxServerError())
			.andExpect(content().string("Branch Reserved"));
	}

	@Test
	@Order(3)
	public void addVisitLimitErrorTest() throws Exception {
		Customer customer = customerRepository.findByName("TestUser").orElseThrow();
		Branch normalBranch2 = branchRepository.findByName("NormalTestBranch2").orElseThrow();
		Branch normalBranch3 = branchRepository.findByName("NormalTestBranch3").orElseThrow();
		Branch limitOverBranch = branchRepository.findByName("NormalTestBranch4").orElseThrow();

		String url = "/api/visits";

		VisitCreateRequest normalVisitCreateRequest2 = VisitCreateRequest
			.builder()
			.customerId(customer.getId())
			.branchId(normalBranch2.getId())
			.content("TestContent")
			.build();
		String reqStr2 = objectMapper.writeValueAsString(normalVisitCreateRequest2);

		ResultActions result2 = mockMvc.perform(post(url).content(reqStr2).contentType(MediaType.APPLICATION_JSON));

		result2.andExpect(status().isOk());

		VisitCreateRequest normalVisitCreateRequest3 = VisitCreateRequest
			.builder()
			.customerId(customer.getId())
			.branchId(normalBranch3.getId())
			.content("TestContent")
			.build();
		String reqStr3 = objectMapper.writeValueAsString(normalVisitCreateRequest3);

		ResultActions result3 = mockMvc.perform(post(url).content(reqStr3).contentType(MediaType.APPLICATION_JSON));

		result3.andExpect(status().isOk());

		VisitCreateRequest normalVisitCreateRequest = VisitCreateRequest
			.builder()
			.customerId(customer.getId())
			.branchId(limitOverBranch.getId())
			.content("TestContent")
			.build();
		String reqStr = objectMapper.writeValueAsString(normalVisitCreateRequest);

		ResultActions result = mockMvc.perform(post(url).content(reqStr).contentType(MediaType.APPLICATION_JSON));

		result.andExpect(status().is5xxServerError())
			.andExpect(content().string("Limit Over"));

	}
}
