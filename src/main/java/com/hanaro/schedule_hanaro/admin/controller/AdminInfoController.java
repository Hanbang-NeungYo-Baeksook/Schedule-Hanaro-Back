package com.hanaro.schedule_hanaro.admin.controller;


import com.hanaro.schedule_hanaro.admin.dto.response.AdminInfoResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminInfoService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminInfoController {

    private final AdminInfoService adminService;

    @GetMapping("/stats")
    public ResponseEntity<AdminInfoResponse> getAdminStats(Authentication authentication) {
        return ResponseEntity.ok(adminService.getAdminStats(authentication));
    }
}

