package com.hanaro.schedule_hanaro.customer.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
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
import com.hanaro.schedule_hanaro.customer.dto.request.RecommendCreateRequest;
import com.hanaro.schedule_hanaro.global.repository.RecommendRepository;
import com.hanaro.schedule_hanaro.global.domain.Recommend;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecommnedControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	RecommendRepository recommendRepository;

	@BeforeAll
	public void beforeAll() {
		for (int i = 1; i < 10; i++) {
			Recommend recommend = Recommend
				.builder()
				.query("TestQuery" + i)
				.response("TestResponse" + i)
				.category(Category.DEPOSIT)
				.queryVector("10101")
				.build();
			recommendRepository.save(recommend);
		}
	}

	@AfterAll
	public void AfterAll() {
		recommendRepository.deleteAll();
	}

	@Test
	public void getRecommendsTest() throws Exception {
		String url = "/api/recommends";
		RecommendCreateRequest recommendCreateRequest = RecommendCreateRequest
			.builder()
			.query("Client Test Query")
			.build();

		String reqStr = objectMapper.writeValueAsString(recommendCreateRequest);

		ResultActions result = mockMvc.perform(post(url).content(reqStr).contentType(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk())
			.andExpect(jsonPath("$.recommends").exists())
			.andExpect(jsonPath("$.tags").exists())
			.andExpect(jsonPath("$.recommends", hasSize(3)))
			.andDo(System.out::println);
		System.out.println("result = " + result);
	}
}
