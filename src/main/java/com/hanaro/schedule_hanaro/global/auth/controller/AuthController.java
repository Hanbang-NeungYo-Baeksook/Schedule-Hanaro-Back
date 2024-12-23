package com.hanaro.schedule_hanaro.global.auth.controller;

import org.apache.tomcat.util.http.HeaderUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.global.auth.dto.request.AuthAdminSignUpRequest;
import com.hanaro.schedule_hanaro.global.auth.dto.request.AuthSignUpRequest;
import com.hanaro.schedule_hanaro.global.auth.dto.request.SignInRequest;
import com.hanaro.schedule_hanaro.global.auth.dto.response.JwtTokenDto;
import com.hanaro.schedule_hanaro.global.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@Operation(summary = "사용자 회원가입", description = "Schedule Hanaro 사용자에게 회원가입 서비스를 제공합니다.")
	@PostMapping("/sign-up")
	public ResponseEntity<?> signUp(@RequestBody AuthSignUpRequest authSignUpRequest) {
		log.info("signUp 컨트롤러 진입");
		authService.signUp(authSignUpRequest);
		return ResponseEntity.ok(null);
	}

	@Operation(summary = "사용자 로그인", description = "Schedule Hanaro 사용자에게 로그인 서비스를 제공합니다.")
	@PostMapping("/sign-in")
	public ResponseEntity<JwtTokenDto> signIn(@RequestBody SignInRequest signInRequest) {
		JwtTokenDto response = authService.signIn(signInRequest);
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "관리자 회원가입", description = "Schedule Hanaro 서비스 관리자 페이지에 관리자를 등록합니다.")
	@PostMapping("/admin/sign-up")
	public ResponseEntity<String> signUpAdmin(@RequestBody AuthAdminSignUpRequest authAdminSignUpRequest) {
		// 관리자 등록 - 테스트용
		return ResponseEntity.ok(authService.adminSignUpAdmin(authAdminSignUpRequest));
	}

	@Operation(summary = "관리자 로그인", description = "Schedule Hanaro 서비스 관리자 페이지에 로그인합니다.")
	@PostMapping("/admin/sign-in")
	public ResponseEntity<JwtTokenDto> signInAdmin(@RequestBody SignInRequest signInRequest) {
		JwtTokenDto response = authService.adminSignIn(signInRequest);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/reissue")
	public ResponseEntity<JwtTokenDto> reissue(HttpServletRequest request) {
		String refreshToken = request.getHeader("Authorization").substring(7);
		JwtTokenDto response = authService.refresh(refreshToken);
		return ResponseEntity.ok().body(response);
	}
}
