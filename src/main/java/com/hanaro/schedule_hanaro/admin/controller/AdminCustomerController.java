package com.hanaro.schedule_hanaro.admin.controller;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInquiryListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerListResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminCustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin-Customer", description = "관리자 고객 관리 API")
@RestController
@RequestMapping("/admin/api/customers")
public class AdminCustomerController {

    @Autowired
    private AdminCustomerService adminCustomerService;

    @Operation(summary = "고객 목록 조회", description = "전체 고객 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<AdminCustomerListResponse> getCustomerList(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        AdminCustomerListResponse response = adminCustomerService.getCustomerList(page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "고객 상세 정보 조회", description = "특정 고객의 상세 정보를 조회합니다.")
    @GetMapping("/{customer-id}")
    public ResponseEntity<AdminCustomerInfoResponse> getCustomerInfo(@PathVariable("customer-id") Long customerId) {
        return ResponseEntity.ok().body(adminCustomerService.findCustomerInfoById(customerId));
    }

    @Operation(summary = "고객 문의 이력 조회", description = "특정 고객의 문의 이력을 조회합니다.")
    @GetMapping("/{customer-id}/content")
    public ResponseEntity<AdminCustomerInquiryListResponse> getCustomerInquiries(@PathVariable("customer-id") Long customerId) {
        AdminCustomerInquiryListResponse response = adminCustomerService.findCustomerInquiryList(customerId);
        return ResponseEntity.ok(response);
    }

}
