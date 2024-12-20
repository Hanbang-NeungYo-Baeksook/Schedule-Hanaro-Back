package com.hanaro.schedule_hanaro.global.auth.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.global.auth.info.UserInfo;
import com.hanaro.schedule_hanaro.global.domain.enums.Role;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
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

	public CustomUserDetails loadUserByUsernameAndRole(UserInfo userInfo) throws UsernameNotFoundException {
		if (userInfo.role().equals(Role.ADMIN)) {
			return adminRepository.findByAuthId(userInfo.id())
				.map(admin -> CustomUserDetails.of(
					admin.getId(),
					admin.getAuthId(),
					admin.getPassword(),
					admin.getRole()
				))
				.orElseThrow(() -> new UsernameNotFoundException("ID가 올바르지 않습니다."));
		} else if (userInfo.role().equals(Role.CUSTOMER)) {
			return customerRepository.findByAuthId(userInfo.id())
				.map(customer -> CustomUserDetails.of(
					customer.getId(),
					customer.getAuthId(),
					customer.getPassword(),
					customer.getRole()
				)).orElseThrow(() -> new UsernameNotFoundException("ID가 올바르지 않습니다."));
		} else
			throw new UsernameNotFoundException("해당하는 회원이 존재하지 않습니다");
	}
}
