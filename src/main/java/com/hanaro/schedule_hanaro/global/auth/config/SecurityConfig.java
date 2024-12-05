package com.hanaro.schedule_hanaro.global.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(session ->
				session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(request ->
				request
					// Security 다 구현하면 줏석 바꾸기
					.requestMatchers("api/**").permitAll()
					.requestMatchers("/test").permitAll()
					.requestMatchers("/hello").permitAll()
					// .requestMatchers("api/v1/auth/sign-up").permitAll()
					// .requestMatchers("api/v1/**").hasAnyRole("customer")
					.anyRequest().authenticated()
			)
			.getOrBuild();
	}

}
