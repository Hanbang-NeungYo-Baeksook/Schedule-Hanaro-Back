package com.hanaro.schedule_hanaro.admin.controller;


import com.hanaro.schedule_hanaro.admin.dto.response.AdminInfoResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminInfoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "관리자 API")
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
