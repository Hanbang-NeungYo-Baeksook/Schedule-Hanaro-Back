package com.hanaro.schedule_hanaro.admin.controller;


import com.hanaro.schedule_hanaro.admin.dto.response.AdminInfoResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminInfoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "관리자 API")
@RestController
@RequestMapping("admin/api/admins")
@RequiredArgsConstructor
public class AdminInfoController {

    private final AdminInfoService adminService;

    @Operation(summary = "관리자 정보 조회", description = "관리자의 전화 문의 / 1:1 문의의 일별, 주별, 월별 접수 건수와 총 접수 건수 통계를 제공합니다.")
    @GetMapping("/stats")
    public ResponseEntity<AdminInfoResponse> getAdminStats(Authentication authentication) {
        return ResponseEntity.ok().body(adminService.getAdminStats(authentication));
    }
}
