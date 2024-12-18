package com.hanaro.schedule_hanaro.admin.controller;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitInquiryInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitNumResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminVisitService;
import com.hanaro.schedule_hanaro.global.domain.Visit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Admin-Visit", description = "관리자 방문 상담 API")
@RestController
@RequestMapping("/admin/api/visits")
@RequiredArgsConstructor
public class AdminVisitController {

    private final AdminVisitService  adminVisitService;

    @Operation(summary = "방문 상담 상세 조회", description = "특정 방문 상담에 대해 상세 정보를 조회합니다.")
    @GetMapping("/{visit-id}/content")
    public ResponseEntity<AdminVisitInquiryInfoResponse> getVisit(@PathVariable("visit-id") Long visitId) {
        Visit visit = adminVisitService.findVisitById(visitId);
        AdminVisitInquiryInfoResponse response = AdminVisitInquiryInfoResponse.from(visit);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "방문 상담 상세 조회", description = "영업점의 현재 방문 상담에 관한 정보들(대기 인원, 번호표 등)을 조회 및 갱신합니다.")
    @PatchMapping("{visit-id}")
    public AdminVisitNumResponse getVisitPageData(@PathVariable("visit-id") Long visitId) {
        return adminVisitService.getVisitPageData(visitId);
    }
}
