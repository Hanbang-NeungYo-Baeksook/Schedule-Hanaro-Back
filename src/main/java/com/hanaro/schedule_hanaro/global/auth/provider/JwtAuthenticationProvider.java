// package com.hanaro.schedule_hanaro.global.auth.provider;
//
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Component;
//
// import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
// import com.hanaro.schedule_hanaro.global.auth.service.CustomUserDetailService;
//
// import lombok.RequiredArgsConstructor;
//
// @Component
// @RequiredArgsConstructor
// public class JwtAuthenticationProvider implements AuthenticationProvider {
//
// 	private final BCryptPasswordEncoder bCryptPasswordEncoder;
// 	private final CustomUserDetailService customUserDetailService;
//
// 	@Override
// 	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
// 		System.out.println("Authenticate Provider 진입 성공 ");
// 		CustomUserDetails userDetails = customUserDetailService.loadUserByUsername(
// 			authentication.getPrincipal().toString());
// 		System.out.println(authentication.getCredentials().toString());
// 		System.out.println(userDetails.getUsername());
// 		if (!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword()))
// 			throw new UsernameNotFoundException("비밀번호가 일치하지 않습니다.");
//
// 		return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
// 			userDetails.getAuthorities());
// 	}
//
// 	@Override
// 	public boolean supports(Class<?> authentication) {
// 		return false;
// 	}
// }
