// package com.hanaro.schedule_hanaro.global;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.hanaro.schedule_hanaro.global.domain.Recommend;
// import com.hanaro.schedule_hanaro.global.domain.enums.Category;
// import com.hanaro.schedule_hanaro.global.repository.RecommendRepository;
// import org.junit.jupiter.api.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.ResultActions;
//
// import java.util.Arrays;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// public class RecommendTest {
//
//     @Autowired
//     private MockMvc mockMvc;
//
//     @Autowired
//     private ObjectMapper objectMapper;
//
//     @Autowired
//     private RecommendRepository recommendRepository;
//
//     private String accessToken;
//
//     @BeforeAll
//     public void setUp() {
//         Recommend recommend1 = Recommend.builder()
//                 .query("예금이 뭔가요?")
//                 .response("예금은 ~~")
//                 .category(Category.DEPOSIT)
//                 .queryVector("1,1,1,0,0,0,0,0")
//                 .build();
//
//         Recommend recommend2 = Recommend.builder()
//                 .query("대출은 어떻게 작동하나요?")
//                 .response("대출은 ~~~")
//                 .category(Category.LOAN)
//                 .queryVector("0,0,0,1,1,1,1,1")
//                 .build();
//
//         recommendRepository.saveAll(Arrays.asList(recommend1, recommend2));
//
//         accessToken = "mockAccessToken";
//     }
//
//     @AfterAll
//     public void afterAll() {
//         recommendRepository.deleteAll();
//     }
//
//     @Test
//     @Order(1)
//     public void testGetRecommends() throws Exception {
//         // Given
//         String query = "예금이 뭔가요?";
//
//         // When
//         ResultActions result = mockMvc.perform(post("/api/recommends")
//                 .header("Authorization", "Bearer " + accessToken)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(new QueryRequest(query))));
//
//         // Then
//         result.andExpect(status().isOk())
//                 .andExpect(jsonPath("$.recommends").isArray())
//                 .andExpect(jsonPath("$.recommends[0].query").value("예금이 뭔가요?"))
//                 .andExpect(jsonPath("$.recommends[0].response").value("예금은 ~~"))
//                 .andExpect(jsonPath("$.tags").exists());
//     }
//
//     @Test
//     @Order(2)
//     public void testGetRecommendsWithEmptyQuery() throws Exception {
//         // Given
//         String query = "";
//
//         // When
//         ResultActions result = mockMvc.perform(post("/api/recommends")
//                 .header("Authorization", "Bearer " + accessToken)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(new QueryRequest(query))));
//
//         // Then
//         result.andExpect(status().isBadRequest());
//     }
// }
//
// class QueryRequest {
//     private String query;
//
//     public QueryRequest(String query) {
//         this.query = query;
//     }
//
//     public String getQuery() {
//         return query;
//     }
//
//     public void setQuery(String query) {
//         this.query = query;
//     }
// }
