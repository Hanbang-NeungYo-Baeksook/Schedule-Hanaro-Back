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
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

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
					.requestMatchers("/api/auth/**", "/api/auth/admin/**").permitAll()
					.requestMatchers("/api/**").hasAuthority("CUSTOMER")
					.requestMatchers("/admin/api/**").hasAuthority("ADMIN")
					.anyRequest().authenticated()
			)
			.addFilterBefore(
				jwtAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class
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
			configuration.setAllowedOriginPatterns(Collections.singletonList("http://localhost:5173"));
			configuration.setAllowCredentials(true);
			return configuration;
		};
	}

}
