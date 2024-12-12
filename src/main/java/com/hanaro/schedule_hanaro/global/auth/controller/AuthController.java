package com.hanaro.schedule_hanaro.global.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.global.auth.dto.request.AuthSignUpRequest;
import com.hanaro.schedule_hanaro.global.auth.service.AuthService;

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

}
