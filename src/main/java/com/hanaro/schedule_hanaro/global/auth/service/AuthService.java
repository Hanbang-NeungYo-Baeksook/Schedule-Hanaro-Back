package com.hanaro.schedule_hanaro.global.auth.service;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.global.auth.dto.response.SignUpResponse;
import com.hanaro.schedule_hanaro.global.auth.info.UserInfo;
import com.hanaro.schedule_hanaro.global.domain.enums.Role;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
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
	private final RedisTemplate<String,String> redisTemplate;


	public JwtTokenDto signIn(SignInRequest signInRequest) {
		return signIn(signInRequest, Role.CUSTOMER);
	}

	public JwtTokenDto adminSignIn(SignInRequest signInRequest) {
		return signIn(signInRequest, Role.ADMIN);

	}

	private JwtTokenDto signIn(SignInRequest signInRequest, Role role) {
		String username = signInRequest.authId();
		String password = signInRequest.password();
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			(new UserInfo(username, role)), password);
		Authentication authentication = authenticationProvider.authenticate(authenticationToken);
		CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
		ValueOperations<String, String> vop = redisTemplate.opsForValue();
		JwtTokenDto response = jwtTokenProvider.generateTokens(customUserDetails.getUsername(),
			customUserDetails.getRole());
		vop.set(username + role.getRole(), response.refreshToken());
		return response;
	}

	public JwtTokenDto refresh(String refreshToken) {
		UserInfo userInfo = jwtTokenProvider.getUserInfoFromToken(refreshToken);
		String username = userInfo.id();
		Role role = userInfo.role();
		ValueOperations<String, String> vop = redisTemplate.opsForValue();
		String savedRefreshToken = vop.get(userInfo.id() + userInfo.role().getRole());
		if (savedRefreshToken == null) {
			throw new GlobalException(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
		} else if (!refreshToken.equals(savedRefreshToken)) {
			throw new GlobalException(ErrorCode.NOT_MATCHED_REFRESH_TOKEN);
		}
		JwtTokenDto response = jwtTokenProvider.generateTokens(username, role);
		vop.set(username + role.getRole(), response.refreshToken());
		return response;
	}

	public SignUpResponse signUp(AuthSignUpRequest authSignUpRequest){
		customerRepository.save(Customer.builder()
			.authId(authSignUpRequest.authId())
			.password(bCryptPasswordEncoder.encode(authSignUpRequest.password()))
			.name(authSignUpRequest.name())
			.phoneNum(authSignUpRequest.phoneNum())
			.birth(authSignUpRequest.birth())
			.gender(Gender.valueOf(authSignUpRequest.gender()))
			.build());
		return SignUpResponse.of();
	}

	public SignUpResponse adminSignUpAdmin(AuthAdminSignUpRequest authAdminSignUpRequest) {
		Branch branch = branchRepository.findById(5L).orElseThrow();
		adminRepository.save(Admin.builder()
				.authId(authAdminSignUpRequest.authId())
				.password(bCryptPasswordEncoder.encode(authAdminSignUpRequest.password()))
				.name(authAdminSignUpRequest.name())
				.branch(branch)
			.build()
		);

		return SignUpResponse.of();
	}

}
