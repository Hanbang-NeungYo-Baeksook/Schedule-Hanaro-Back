package com.hanaro.schedule_hanaro.global.auth.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.hanaro.schedule_hanaro.global.auth.filter.JwtAuthenticationFilter;
import com.hanaro.schedule_hanaro.global.auth.filter.JwtExceptionFilter;
import com.hanaro.schedule_hanaro.global.auth.handler.CustomAuthenticationEntryPointHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			// .anonymous(AbstractHttpConfigurer::disable)
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(request ->
				request
					// Swagger 경로에 대한 접근 허용
					.requestMatchers(
						"/swagger-ui/**",
						"/v3/api-docs/**",
						"/swagger-ui.html"
					).permitAll()
					// WebSocket 엔드포인트 허용
					.requestMatchers("/ws/test").permitAll()
					.requestMatchers("/api/auth/**", "/api/auth/admin/**").permitAll()
					.requestMatchers("/api/**").hasAuthority("CUSTOMER")
					.requestMatchers("/admin/api/**").hasAuthority("ADMIN")
					.anyRequest().authenticated()
			)
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(customAuthenticationEntryPointHandler)
			)
			.addFilterBefore(
				jwtAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class
			)
			.addFilterBefore(
				new JwtExceptionFilter(),
				JwtAuthenticationFilter.class
			)
			.cors(
				httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
			.build();
	}

	CorsConfigurationSource corsConfigurationSource() {
		return request -> {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.setAllowedHeaders(Collections.singletonList("*"));
			configuration.setAllowedMethods(Collections.singletonList("*"));
			// configuration.setAllowedOriginPatterns(Collections.singletonList("http://localhost:5173"));
			configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
			configuration.setAllowCredentials(true);
			return configuration;
		};
	}

}
