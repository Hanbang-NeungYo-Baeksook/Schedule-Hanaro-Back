package com.hanaro.schedule_hanaro.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.auth.dto.request.AuthSignUpRequest;
import com.hanaro.schedule_hanaro.auth.service.AuthService;
import com.hanaro.schedule_hanaro.global.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/sign-up")
	public ResponseDto<?> sginUp(@RequestBody AuthSignUpRequest authSignUpRequest){
		log.info("signUp 컨트롤러 진입");
		authService.signUp(authSignUpRequest);
		return ResponseDto.created(null);
	}

}
