package com.hanaro.schedule_hanaro.global.auth.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
import com.hanaro.schedule_hanaro.global.auth.info.UserInfo;
import com.hanaro.schedule_hanaro.global.auth.service.CustomUserDetailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final CustomUserDetailService customUserDetailService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		System.out.println("Authenticate Provider 진입 성공 ");
		System.out.println(authentication.getPrincipal().toString());
		if (authentication.getPrincipal().getClass().equals(String.class)) {
			System.out.println("로그인 시작");
			return authOfLogin(authentication);
		} else {
			return authAfterLogin((UserInfo) authentication.getPrincipal());
		}
	}

	private Authentication authOfLogin(Authentication authentication) {
		CustomUserDetails customUserDetails = customUserDetailService.loadUserByUsername(
			authentication.getPrincipal().toString());

		System.out.println(customUserDetails.getUsername());
		if (!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), customUserDetails.getPassword()))
			throw new UsernameNotFoundException("비밀번호가 일치하지 않습니다.");

		System.out.println(customUserDetails.getAuthorities());
		return new UsernamePasswordAuthenticationToken(customUserDetails, customUserDetails.getPassword(),
			customUserDetails.getAuthorities());
	}

	private Authentication authAfterLogin(UserInfo userInfo) {
		System.out.println(userInfo.id()+userInfo.role());
		CustomUserDetails customUserDetails = customUserDetailService.loadUserByUsername(
			userInfo.id());
		return new UsernamePasswordAuthenticationToken(customUserDetails, customUserDetails.getPassword(),
			customUserDetails.getAuthorities());
	}
	@Override
	public boolean supports(Class<?> authentication) {
		return false;
	}
}
