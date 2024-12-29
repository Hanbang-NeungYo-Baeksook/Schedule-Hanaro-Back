package com.hanaro.schedule_hanaro.global.auth.controller;

import java.util.Arrays;

import com.hanaro.schedule_hanaro.global.auth.dto.response.AdminSignInResponse;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.global.auth.dto.request.AuthAdminSignUpRequest;
import com.hanaro.schedule_hanaro.global.auth.dto.request.AuthSignUpRequest;
import com.hanaro.schedule_hanaro.global.auth.dto.request.SignInRequest;
import com.hanaro.schedule_hanaro.global.auth.dto.response.JwtTokenDto;
import com.hanaro.schedule_hanaro.global.auth.dto.response.SignInResponse;
import com.hanaro.schedule_hanaro.global.auth.dto.response.SignUpResponse;
import com.hanaro.schedule_hanaro.global.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final AdminRepository adminRepository;

	@Operation(summary = "사용자 회원가입", description = "Schedule Hanaro 사용자에게 회원가입 서비스를 제공합니다.")
	@PostMapping("/sign-up")
	public ResponseEntity<SignUpResponse> signUp(@RequestBody AuthSignUpRequest authSignUpRequest) {
		log.info("signUp 컨트롤러 진입");
		return ResponseEntity.ok().body(
			authService.signUp(authSignUpRequest));
	}

	@Operation(summary = "사용자 로그인", description = "Schedule Hanaro 사용자에게 로그인 서비스를 제공합니다.")
	@PostMapping("/sign-in")
	public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest signInRequest, HttpServletResponse response) {
		JwtTokenDto tokenDto = authService.signIn(signInRequest);

		Cookie cookie = new Cookie("refresh-token", tokenDto.refreshToken());
		cookie.setMaxAge(3*60*60);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);

		return ResponseEntity.ok().body(SignInResponse.of(tokenDto.accessToken(), tokenDto.refreshToken()));
	}

	@Operation(summary = "관리자 회원가입", description = "Schedule Hanaro 서비스 관리자 페이지에 관리자를 등록합니다.")
	@PostMapping("/admin/sign-up")
	public ResponseEntity<SignUpResponse> signUpAdmin(@RequestBody AuthAdminSignUpRequest authAdminSignUpRequest) {
		// 관리자 등록 - 테스트용
		return ResponseEntity.ok().body(authService.adminSignUpAdmin(authAdminSignUpRequest));
	}

	@Operation(summary = "관리자 로그인", description = "Schedule Hanaro 서비스 관리자 페이지에 로그인합니다.")
	@PostMapping("/admin/sign-in")
	public ResponseEntity<AdminSignInResponse> signInAdmin(@RequestBody SignInRequest signInRequest) {
		Admin admin = adminRepository.findByAuthId(signInRequest.authId())
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_ADMIN));

		JwtTokenDto tokenDto = authService.adminSignIn(signInRequest);
		
		AdminSignInResponse response = AdminSignInResponse.of(
			tokenDto,
			admin.getId(),
			admin.getName(),
			admin.getBranch().getName()
		);
		
		log.info("Admin login successful - authId: {}, name: {}", admin.getAuthId(), admin.getName());
		
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/reissue")
	public ResponseEntity<JwtTokenDto> reissue(HttpServletRequest request) {
		String refreshToken = request.getHeader("Authorization").substring(7);
		String refreshToken1 = Arrays.toString(request.getCookies());
		System.out.println(refreshToken1);
		JwtTokenDto response = authService.refresh(refreshToken);
		return ResponseEntity.ok().body(response);
	}
}
