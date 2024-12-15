package com.hanaro.schedule_hanaro.admin.controller;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInquiryListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCustomerInfoResponse;
import com.hanaro.schedule_hanaro.admin.service.AdminCustomerService;

@RestController
@RequestMapping("/admin/api/customers")
public class AdminCustomerController {

    @Autowired
    private AdminCustomerService adminCustomerService;

    @GetMapping("/{customer_id}")
    public ResponseEntity<AdminCustomerInfoResponse> getCustomerInfo(@PathVariable Long customer_id) {
        return ResponseEntity.ok().body(adminCustomerService.findCustomerInfoById(customer_id));
    }

    @GetMapping("/customers/{customer_id}/content")
    public ResponseEntity<AdminCustomerInquiryListResponse> getCustomerInquiries(@PathVariable Long customer_id) {
        AdminCustomerInquiryListResponse response = adminCustomerService.findCustomerInquiryList(customer_id);
        return ResponseEntity.ok(response);
    }

}