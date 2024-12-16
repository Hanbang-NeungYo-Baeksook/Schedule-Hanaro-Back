package com.hanaro.schedule_hanaro.admin.controller;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminInfoResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminInfoService;
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



    @GetMapping("/{admin-id}/stats")
    public ResponseEntity<AdminInfoResponse> getAdminStats(@PathVariable("admin-id") Long adminId) {
        return ResponseEntity.ok(adminService.getAdminStats(adminId));
    }
}

