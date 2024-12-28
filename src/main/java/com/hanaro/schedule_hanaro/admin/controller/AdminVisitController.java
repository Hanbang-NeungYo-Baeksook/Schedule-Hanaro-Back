package com.hanaro.schedule_hanaro.admin.controller;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitInquiryInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitNumResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitStatusUpdateResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminVisitService;
import com.hanaro.schedule_hanaro.global.domain.Visit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "Admin-Visit", description = "관리자 방문 상담 API")
@RestController
@RequestMapping("/admin/api/visits")
@RequiredArgsConstructor
public class AdminVisitController {

    private static final Logger log = LoggerFactory.getLogger(AdminVisitController.class);

    private final AdminVisitService  adminVisitService;

    @Operation(summary = "방문 상담 상세 조회", description = "특정 방문 상담에 대해 상세 정보를 조회합니다.")
    @GetMapping("/{visit-id}/content")
    public ResponseEntity<AdminVisitInquiryInfoResponse> getVisit(@PathVariable("visit-id") Long visitId) {
        Visit visit = adminVisitService.findVisitById(visitId);
        AdminVisitInquiryInfoResponse response = AdminVisitInquiryInfoResponse.from(visit);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "방문 상담 상태 변경", description = "특정 방문 상담의 상태를 진행중으로 변경하고, 창구의 대기 상태를 갱신합니다.")
    @PatchMapping("/{visit-id}/status")
    public ResponseEntity<AdminVisitStatusUpdateResponse> updateVisitStatus(
            @PathVariable("visit-id") Long visitId
    ) {
        log.info("=== Start updateVisitStatus API ===");
        log.info("Visit ID: {}", visitId);
        
        AdminVisitStatusUpdateResponse response = adminVisitService.updateVisitStatus(visitId);
        log.info("=== End updateVisitStatus API ===");
        return ResponseEntity.ok().body(response);
    }



    @Operation(summary = "현재 진행중인 상담 조회", description = "해당 섹션에서 현재 진행중인 상담을 조회합니다.")
    @GetMapping("/sections/{section-id}/current")
    public ResponseEntity<AdminVisitStatusUpdateResponse> getCurrentVisit(
            @PathVariable("section-id") Long sectionId
    ) {
        AdminVisitStatusUpdateResponse response = adminVisitService.getCurrentVisit(sectionId);
        return ResponseEntity.ok().body(response);
    }
}
