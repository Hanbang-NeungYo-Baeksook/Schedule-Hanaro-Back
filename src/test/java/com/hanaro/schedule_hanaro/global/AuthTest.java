package com.hanaro.schedule_hanaro.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaro.schedule_hanaro.global.auth.dto.request.AuthSignUpRequest;
import com.hanaro.schedule_hanaro.global.auth.dto.request.SignInRequest;
import com.hanaro.schedule_hanaro.global.auth.dto.response.JwtTokenDto;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeAll
    public void setUp() {
    }

    @AfterAll
    public void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void testSignUp() throws Exception {
        // Given
        AuthSignUpRequest signUpRequest = AuthSignUpRequest.builder()
                .authId("testUser")
                .password("password123")
                .name("Test User")
                .phoneNum("01012345678")
                .birth(LocalDate.of(2000, 1, 1))
                .gender("FEMALE")
                .build();

        // When
        ResultActions result = mockMvc.perform(post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)));

        // Then
        result.andExpect(status().isOk())
                .andExpect(content().string("회원가입이 완료되었습니다")); // 회원가입 성공 시 내용 없음

        Assertions.assertTrue(customerRepository.findByAuthId("testUser").isPresent());
    }

    @Test
    @Order(2)
    public void testSignIn() throws Exception {
        // Given
        Customer customer = Customer.builder()
                .authId("testUser")
                .password("$2a$10$encryptedPassword")
                .name("Test User")
                .phoneNum("01012345678")
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.FEMALE)
                .build();
        customerRepository.save(customer);

        SignInRequest signInRequest = new SignInRequest("testUser", "password123");

        // When
        ResultActions result = mockMvc.perform(post("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @Order(3)
    public void testSignInWithInvalidCredentials() throws Exception {
        // Given
        SignInRequest signInRequest = new SignInRequest("invalidUser", "wrongPassword");

        // When
        ResultActions result = mockMvc.perform(post("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)));

        // Then
        result.andExpect(status().isUnauthorized());
    }
}
