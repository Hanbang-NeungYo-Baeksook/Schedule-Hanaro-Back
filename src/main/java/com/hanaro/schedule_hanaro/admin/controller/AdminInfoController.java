package com.hanaro.schedule_hanaro.admin.controller;

import java.security.Principal;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminInfoResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminInfoService;
import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminInfoController {

    private final AdminInfoService adminService;



    @GetMapping("/stats")
    public ResponseEntity<AdminInfoResponse> getAdminStats(Principal principal) {
        return ResponseEntity.ok(adminService.getAdminStats(principal));
    }
}

