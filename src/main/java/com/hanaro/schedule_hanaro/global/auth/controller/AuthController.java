package com.hanaro.schedule_hanaro.global.auth.controller;

import java.security.Principal;

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

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/sign-up")
	public ResponseEntity<?> signUp(@RequestBody AuthSignUpRequest authSignUpRequest){
		log.info("signUp 컨트롤러 진입");
		authService.signUp(authSignUpRequest);
		return ResponseEntity.ok(null);
	}

	@PostMapping("/sign-in")
	public ResponseEntity<JwtTokenDto> signIn(@RequestBody SignInRequest signInRequest) {
		JwtTokenDto response = authService.signIn(signInRequest);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/admin/sign-up")
	public ResponseEntity<String> signUpAdmin(@RequestBody AuthAdminSignUpRequest authAdminSignUpRequest) {
		// 관리자 등록 - 테스트용
		return ResponseEntity.ok(authService.adminSignUpAdmin(authAdminSignUpRequest));
	}

	@PostMapping("/admin/sign-in")
	public ResponseEntity<JwtTokenDto> signInAdmin(@RequestBody SignInRequest signInRequest) {
		JwtTokenDto response = authService.signIn(signInRequest);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/reissue")
	public ResponseEntity<JwtTokenDto> reissueToken(HttpServletRequest reissueRequest) {
		return ResponseEntity.ok().body(authService.reissueToken(reissueRequest));
	}

}
