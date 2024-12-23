// package com.hanaro.schedule_hanaro.customer.controller;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.hanaro.schedule_hanaro.customer.dto.request.InquiryCreateRequest;
// import com.hanaro.schedule_hanaro.customer.dto.response.InquiryCreateResponse;
// import com.hanaro.schedule_hanaro.customer.dto.response.InquiryListResponse;
// import com.hanaro.schedule_hanaro.customer.dto.response.InquiryReplyDetailResponse;
// import com.hanaro.schedule_hanaro.customer.dto.response.InquiryResponse;
// import com.hanaro.schedule_hanaro.customer.service.InquiryService;
// import com.hanaro.schedule_hanaro.global.auth.filter.JwtAuthenticationFilter;
// import com.hanaro.schedule_hanaro.global.auth.provider.JwtTokenProvider;
// import com.hanaro.schedule_hanaro.global.domain.enums.Category;
// import org.junit.jupiter.api.Order;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestMethodOrder;
// import org.junit.jupiter.api.MethodOrderer;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.ResultActions;
// import org.springframework.boot.test.mock.mockito.MockBean;
//
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.util.List;
//
// @SpringBootTest
// @AutoConfigureMockMvc(addFilters = false)
// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// public class InquiryControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@MockBean
// 	private InquiryService inquiryService;
//
// 	@MockBean
// 	private JwtAuthenticationFilter jwtAuthenticationFilter;
//
// 	@MockBean
// 	private JwtTokenProvider jwtTokenProvider;
//
// 	@Test
// 	@Order(1)
// 	@WithMockUser(username = "customer1", roles = {"CUSTOMER"})
// 	public void createInquiryTest() throws Exception {
// 		when(inquiryService.createInquiry(any(), any())).thenReturn(
// 			InquiryCreateResponse.builder()
// 				.inquiryId(1L)
// 				.build()
// 		);
//
// 		InquiryCreateRequest request = InquiryCreateRequest.builder()
// 			.category(Category.SIGNIN)
// 			.content("This is a test inquiry.")
// 			.build();
//
// 		String requestBody = objectMapper.writeValueAsString(request);
//
// 		mockMvc.perform(post("/api/inquiries")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(requestBody))
// 			.andExpect(status().isCreated())
// 			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(jsonPath("$.inquiry_id").value(1L));
// 	}
//
// 	@Test
// 	@Order(2)
// 	@WithMockUser(username = "customer1", roles = {"CUSTOMER"})
// 	public void getInquiriesTest() throws Exception {
// 		InquiryListResponse mockResponse = InquiryListResponse.builder()
// 			.data(List.of(
// 				InquiryListResponse.InquiryData.builder()
// 					.inquiryId(1L)
// 					.inquiryNum(101)
// 					.category("SIGNIN")
// 					.status("PENDING")
// 					.content("This is a test inquiry")
// 					.tags(List.of("tag1", "tag2"))
// 					.build()
// 			))
// 			.pagination(InquiryListResponse.Pagination.builder()
// 				.currentPage(1)
// 				.pageSize(5)
// 				.hasNext(false)
// 				.build())
// 			.build();
//
// 		when(inquiryService.getInquiryList(anyString(), anyInt(), anyInt()))
// 			.thenReturn(mockResponse);
//
// 		mockMvc.perform(get("/api/inquiries")
// 				.param("status", "PENDING")
// 				.param("page", "1")
// 				.param("size", "5"))
// 			.andExpect(status().isOk())
// 			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(jsonPath("$.data[0].inquiry_id").value(1L))
// 			.andExpect(jsonPath("$.data[0].inquiry_num").value(101))
// 			.andExpect(jsonPath("$.data[0].category").value("SIGNIN"))
// 			.andExpect(jsonPath("$.pagination.currentPage").value(1))
// 			.andExpect(jsonPath("$.pagination.hasNext").value(false));
// 	}
//
// 	@Order(3)
// 	@WithMockUser(username = "customer1", roles = {"CUSTOMER"})
// 	public void getInquiryDetailTest() throws Exception {
// 		InquiryResponse mockResponse = InquiryResponse.builder()
// 			.inquiryId(1L)
// 			.inquiryNum(101)
// 			.customerName("Customer1")
// 			.category("SIGNIN")
// 			.status("PENDING")
// 			.content("This is a test inquiry detail")
// 			.tags(List.of("tag1", "tag2"))
// 			.build();
//
// 		when(inquiryService.getInquiryDetail(1L))
// 			.thenReturn(mockResponse);
//
// 		mockMvc.perform(get("/api/inquiries/{inquiry-id}", 1L))
// 			.andExpect(status().isOk())
// 			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(jsonPath("$.inquiry_id").value(1L))
// 			.andExpect(jsonPath("$.inquiry_num").value(101))
// 			.andExpect(jsonPath("$.customer_name").value("Customer1"))
// 			.andExpect(jsonPath("$.content").value("This is a test inquiry detail"));
// 	}
//
// 	@Test
// 	@Order(4)
// 	@WithMockUser(username = "customer1", roles = {"CUSTOMER"})
// 	public void getInquiryReplyDetailTest() throws Exception {
// 		InquiryReplyDetailResponse mockResponse = InquiryReplyDetailResponse.builder()
// 			.content("This is a test inquiry reply")
// 			.status("COMPLETED")
// 			.reply("This is a reply message")
// 			.tag(List.of("tag1", "tag2"))
// 			.build();
//
// 		when(inquiryService.getInquiryReplyDetail(1L))
// 			.thenReturn(mockResponse);
//
// 		mockMvc.perform(get("/api/inquiries/{inquiry-id}/reply", 1L))
// 			.andExpect(status().isOk())
// 			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(jsonPath("$.content").value("This is a test inquiry reply"))
// 			.andExpect(jsonPath("$.status").value("COMPLETED"))
// 			.andExpect(jsonPath("$.reply").value("This is a reply message"))
// 			.andExpect(jsonPath("$.tag[0]").value("tag1"));
// 	}
//
// 	@Test
// 	@Order(5)
// 	@WithMockUser(username = "customer1", roles = {"CUSTOMER"})
// 	public void cancelInquiryTest() throws Exception {
// 		ResultActions result = mockMvc.perform(delete("/api/inquiries/{inquiry-id}", 1L));
//
// 		result.andExpect(status().isOk());
// 	}
// }
