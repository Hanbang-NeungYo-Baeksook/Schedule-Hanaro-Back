package com.hanaro.schedule_hanaro.global.auth.service;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import com.hanaro.schedule_hanaro.global.repository.BranchRepository;
import com.hanaro.schedule_hanaro.global.auth.dto.request.AuthAdminSignUpRequest;
import com.hanaro.schedule_hanaro.global.auth.dto.request.AuthSignUpRequest;
import com.hanaro.schedule_hanaro.global.auth.dto.request.SignInRequest;
import com.hanaro.schedule_hanaro.global.auth.dto.response.JwtTokenDto;
import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
import com.hanaro.schedule_hanaro.global.auth.provider.JwtAuthenticationProvider;
import com.hanaro.schedule_hanaro.global.auth.provider.JwtTokenProvider;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final CustomerRepository customerRepository;
	private final AdminRepository adminRepository;
	private final BranchRepository branchRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JwtAuthenticationProvider authenticationProvider;
	private final JwtTokenProvider jwtTokenProvider;


	public JwtTokenDto signIn(SignInRequest signInRequest) {
		String username = signInRequest.authId();
		String password = signInRequest.password();
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
			password);
		Authentication authentication = authenticationProvider.authenticate(authenticationToken);
		CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
		return jwtTokenProvider.generateTokens(customUserDetails.getUsername(), customUserDetails.getRole());
	}

	public void signUp(AuthSignUpRequest authSignUpRequest){
		customerRepository.save(Customer.builder()
			.authId(authSignUpRequest.authId())
			.password(bCryptPasswordEncoder.encode(authSignUpRequest.password()))
			.name(authSignUpRequest.name())
			.phoneNum(authSignUpRequest.phoneNum())
			.birth(authSignUpRequest.birth())
			.gender(Gender.valueOf(authSignUpRequest.gender()))
			.build());
	}

	public String adminSignUpAdmin(AuthAdminSignUpRequest authAdminSignUpRequest) {
		Branch branch = branchRepository.findById(1L).orElseThrow();
		adminRepository.save(Admin.builder()
				.authId(authAdminSignUpRequest.authId())
				.password(bCryptPasswordEncoder.encode(authAdminSignUpRequest.password()))
				.name(authAdminSignUpRequest.name())
				.branch(branch)
			.build()
		);

		return "Success";
	}

	public JwtTokenDto reissueToken(HttpServletRequest request) {
		String refreshToken = request.getHeader("Authorization");
		isTokenPresent(refreshToken);
		String token = refreshToken.substring(7);
		UserInfo userInfo = jwtTokenProvider.getUsernameFromToken(token);
		return jwtTokenProvider.generateTokens(userInfo.id(), userInfo.role());
	}

	private void isTokenPresent(String token) {
		if (token.isEmpty()) {
			throw new AuthException(ErrorCode.EMPTY_JWT);
		}
	}
}
