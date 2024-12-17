package com.hanaro.schedule_hanaro.global.auth.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

	private final CustomerRepository customerRepository;
	private final AdminRepository adminRepository;

	@Override
	public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return customerRepository.findByAuthId(username)
			.map(customer -> CustomUserDetails.of(
				customer.getId(),
				customer.getAuthId(),
				customer.getPassword(),
				customer.getRole()
			))
			.orElseGet(() -> {
				return adminRepository.findByAuthId(username)
					.map(admin -> CustomUserDetails.of(
						admin.getId(),
						admin.getAuthId(),
						admin.getPassword(),
						admin.getRole()
					))
					.orElseThrow(() -> new UsernameNotFoundException("ID가 올바르지 않습니다."));
			});
	}
}
