package com.hanaro.schedule_hanaro.admin.service;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminInfoDto;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
import com.hanaro.schedule_hanaro.admin.repository.AdminRepository;
import com.hanaro.schedule_hanaro.customer.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminInfoService {
    private final AdminRepository adminRepository;
    private final InquiryRepository inquiryRepository;


    public AdminInfoResponse getAdminStats(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        AdminInquiryStatsDto phoneInquiryStats = inquiryRepository.findStatsByAdminId(adminId);
        AdminInquiryStatsDto oneToOneInquiryStats = inquiryRepository.findStatsByAdminId(adminId);

        return AdminInfoResponse.from(admin ,phoneInquiryStats, oneToOneInquiryStats);
    }
}
