package com.hanaro.schedule_hanaro.customer.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import com.hanaro.schedule_hanaro.global.repository.BranchRepository;
import com.hanaro.schedule_hanaro.global.repository.CsVisitRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.repository.VisitRepository;
import com.hanaro.schedule_hanaro.customer.service.VisitService;
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

	@Autowired
	VisitService visitService;

	@BeforeAll
	public void beforeAll() {
		Customer customer = Customer
			.builder()
			.authId("TestAuthId")
			.name("TestUser")
			.password("TestPassword")
			.phoneNum("01012341234")
			.birth(LocalDate.of(2002, 4, 15))
			.gender(Gender.FEMALE)
			.build();
		customerRepository.save(customer);

		for (int i = 2; i <= 7; i++) {
			customerRepository.save(Customer
				.builder()
				.authId("TestAuthId" + i)
				.name("TestUser" + i)
				.password("TestPassword")
				.phoneNum("01012341234")
				.birth(LocalDate.of(2002, 4, 15))
				.gender(Gender.FEMALE)
				.build()
			);
		}

		for (int i = 1; i <= 6; i++) {
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

		for (int i = 1; i <= 6; i++) {
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
		for (int i = 1; i <= 6; i++) {
			Branch branch = branchRepository.findByName("NormalTestBranch" + i).orElseThrow();
			CsVisit csVisit = csVisitRepository.findByBranchId(branch.getId()).orElseThrow();
			List<Visit> visits = visitRepository.findAllByBranchId(branch.getId());
			csVisitRepository.delete(csVisit);
			visitRepository.deleteAll(visits);
			branchRepository.delete(branch);
		}
		Branch branch = branchRepository.findByName("AbnormalTestBranch").orElseThrow();
		CsVisit csVisit = csVisitRepository.findByBranchId(branch.getId()).orElseThrow();
		List<Visit> visits = visitRepository.findAllByBranchId(branch.getId());
		csVisitRepository.delete(csVisit);
		visitRepository.deleteAll(visits);
		branchRepository.delete(branch);

		Customer customer = customerRepository.findByAuthId("TestAuthId").orElseThrow();
		customerRepository.delete(customer);

		Customer customer2 = customerRepository.findByAuthId("TestAuthId2").orElseThrow();
		customerRepository.delete(customer2);

		Customer customer3 = customerRepository.findByAuthId("TestAuthId3").orElseThrow();
		customerRepository.delete(customer3);

		Customer customer4 = customerRepository.findByAuthId("TestAuthId4").orElseThrow();
		customerRepository.delete(customer4);

		Customer customer5 = customerRepository.findByAuthId("TestAuthId5").orElseThrow();
		customerRepository.delete(customer5);

		Customer customer6 = customerRepository.findByAuthId("TestAuthId6").orElseThrow();
		customerRepository.delete(customer6);

		Customer customer7 = customerRepository.findByAuthId("TestAuthId7").orElseThrow();
		customerRepository.delete(customer7);
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

	@Test
	@Order(4)
	public void getVisitDetailTest() throws Exception {
		Customer customer = customerRepository.findByName("TestUser2").orElseThrow();
		Branch branch = branchRepository.findByName("NormalTestBranch5").orElseThrow();
		Long visitId = visitService.addVisitReservation(VisitCreateRequest
			.builder()
			.customerId(customer.getId())
			.branchId(branch.getId())
			.content("Test Content")
			.build()
		);
		CsVisit csVisit = csVisitRepository.findByBranchIdAndDate(branch.getId(), LocalDate.now()).orElseThrow();

		String url = "/api/visits/" + visitId;
		ResultActions result = mockMvc.perform(get(url));

		result.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.visit_id").value(visitId))
			.andExpect(jsonPath("$.branch_name").value(branch.getName()))
			.andExpect(jsonPath("$.visit_num").value(csVisit.getTotalNum()))
			.andExpect(jsonPath("$.waiting_amount").value(csVisit.getWaitAmount() - 1))
			.andExpect(jsonPath("$.waiting_time").exists())
			.andDo(System.out::print);
	}

	@Test
	@Order(5)
	public void getVistListTest() throws Exception {
		Customer customer = customerRepository.findByName("TestUser3").orElseThrow();

		List<Branch> branchList = new ArrayList<>();
		branchList.add(branchRepository.findByName("NormalTestBranch1").orElseThrow());
		branchList.add(branchRepository.findByName("NormalTestBranch2").orElseThrow());
		branchList.add(branchRepository.findByName("NormalTestBranch3").orElseThrow());

		List<Long> visitIdList = new ArrayList<>();
		for (Branch branch : branchList) {
			visitIdList.add(visitService.addVisitReservation(VisitCreateRequest
				.builder()
				.customerId(customer.getId())
				.branchId(branch.getId())
				.content("Test Content")
				.build()
			));
		}

		List<Visit> visitList = new ArrayList<>();
		for (Long visitId : visitIdList) {
			visitList.add(visitRepository.findById(visitId).orElseThrow());
		}

		String url = "/api/visits";
		ResultActions result = mockMvc.perform(get(url).param("customerId", String.valueOf(customer.getId())));
		result.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.data").exists())
			.andExpect(jsonPath("$.data", hasSize(3)));

		for (int i = 0; i < 3; i++) {
			result
				.andExpect(jsonPath(String.format("$.data[%d].visit_id", i)).value(visitIdList.get(i)))
				.andExpect(jsonPath(String.format("$.data[%d].branch_name", i)).value(branchList.get(i).getName()))
				.andExpect(jsonPath(String.format("$.data[%d].visit_num", i)).value(visitList.get(i).getNum()))
				.andExpect(jsonPath(String.format("$.data[%d].waiting_amount", i)).exists())
				.andExpect(jsonPath(String.format("$.data[%d].waiting_time", i)).exists())
				.andDo(System.out::print);
		}
	}

	@Test
	@Order(6)
	public void addMutipleVisitInOneBranchTest() throws Exception {
		List<Customer> customerList = new ArrayList<>();
		customerList.add(customerRepository.findByName("TestUser4").orElseThrow());
		customerList.add(customerRepository.findByName("TestUser5").orElseThrow());
		customerList.add(customerRepository.findByName("TestUser6").orElseThrow());
		customerList.add(customerRepository.findByName("TestUser7").orElseThrow());

		Branch branch = branchRepository.findByName("NormalTestBranch6").orElseThrow();

		List<Long> visitIdList = new ArrayList<>();

		String addUrl = "/api/visits";
		for (int i = 0; i < customerList.size(); i++) {
			VisitCreateRequest visitCreateRequest = VisitCreateRequest
				.builder()
				.customerId(customerList.get(i).getId())
				.branchId(branch.getId())
				.content("Test Content!")
				.build();

			String reqStr = objectMapper.writeValueAsString(visitCreateRequest);

			ResultActions result = mockMvc.perform(
				post(addUrl).content(reqStr).contentType(MediaType.APPLICATION_JSON)
			);

			result.andExpect(status().isOk());
			String responseContent = result.andReturn().getResponse().getContentAsString();
			visitIdList.add(Long.valueOf(responseContent));
		}

		for (int i = 0; i < visitIdList.size(); i++) {
			String getUrl = "/api/visits/" + visitIdList.get(i);
			ResultActions result = mockMvc.perform(get(getUrl));
			result.andExpect(status().isOk())
				.andExpect(jsonPath("$.visit_num").value(i + 1));

		}

	}
}
