// package com.hanaro.schedule_hanaro.global.auth.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
// import com.hanaro.schedule_hanaro.global.auth.filter.JwtAuthenticationFilter;
// import com.hanaro.schedule_hanaro.global.auth.provider.JwtAuthenticationProvider;
// import com.hanaro.schedule_hanaro.global.auth.provider.JwtTokenProvider;
//
// import lombok.RequiredArgsConstructor;
//
// @Configuration
// @EnableWebSecurity
// @RequiredArgsConstructor
// public class SecurityConfig {
//
// 	private final JwtTokenProvider jwtTokenProvider;
// 	private final JwtAuthenticationProvider jwtAuthenticationProvider;
//
// 	@Bean
// 	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
// 		return http
// 			.csrf(AbstractHttpConfigurer::disable)
// 			.httpBasic(AbstractHttpConfigurer::disable)
// 			.sessionManagement(session ->
// 				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
// 			)
// 			.authorizeHttpRequests(request ->
// 					request
// 						.requestMatchers("/api/auth/sign-up").permitAll()
// 						.requestMatchers("/api/auth/sign-in").permitAll()
// 						// .requestMatchers("/api/**").hasAnyRole("USER")
// 						.requestMatchers("/admin/api/**").hasAnyRole("ADMIN")
// 						.anyRequest().authenticated()
// 				// .anyRequest().permitAll()
// 			)
// 			// .logout(logout ->
// 			// 	logout
// 			// 		.logoutSuccessUrl("api/auth/sign-out")
// 			// )
// 			.addFilterAfter(
// 				new JwtAuthenticationFilter(jwtTokenProvider, jwtAuthenticationProvider),
// 				UsernamePasswordAuthenticationFilter.class
// 			)
// 			.getOrBuild();
// 	}
//
// }
