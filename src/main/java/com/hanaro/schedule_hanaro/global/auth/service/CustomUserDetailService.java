// package com.hanaro.schedule_hanaro.global.auth.service;
//
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;
//
// import com.hanaro.schedule_hanaro.admin.repository.AdminRepository;
// import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;
// import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
// import com.hanaro.schedule_hanaro.global.domain.Customer;
//
// import lombok.RequiredArgsConstructor;
//
// @Service
// @RequiredArgsConstructor
// public class CustomUserDetailService implements UserDetailsService {
//
// 	private final CustomerRepository customerRepository;
// 	private final AdminRepository adminRepository;
//
// 	@Override
// 	public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
// 		Customer customer = customerRepository.findByAuthId(username).orElseThrow();
// 		return CustomUserDetails.of(customer.getId(), customer.getAuthId(), customer.getPassword(),
// 			customer.getRole());
// 	}
// }
