package com.hanaro.schedule_hanaro.admin.controller;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminVisitInquiryInfoResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminVisitService;
import com.hanaro.schedule_hanaro.global.domain.Visit;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/visits")
@RequiredArgsConstructor
public class AdminVisitController {
    private final AdminVisitService adminVisitService;

    @GetMapping("/{visit_id}/content")
    public ResponseEntity<AdminVisitInquiryInfoResponse> getVisit(@PathVariable Long visit_id) {
        Visit visit = adminVisitService.findVisitById(visit_id);
        AdminVisitInquiryInfoResponse response = AdminVisitInquiryInfoResponse.from(visit);
        return ResponseEntity.ok().body(response);
    }
}
