package com.hanaro.schedule_hanaro.admin.service;


import com.hanaro.schedule_hanaro.admin.dto.response.AdminInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
import com.hanaro.schedule_hanaro.global.repository.CallRepository;
import com.hanaro.schedule_hanaro.global.repository.InquiryRepository;
import com.hanaro.schedule_hanaro.global.domain.Admin;
import com.hanaro.schedule_hanaro.global.utils.PrincipalUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminInfoService {
    private final AdminRepository adminRepository;
    private final InquiryRepository inquiryRepository;
    private final CallRepository callRepository;

    public AdminInfoResponse getAdminStats(Authentication authentication) {
        Long id = PrincipalUtils.getId(authentication);
        Admin admin = adminRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_ADMIN));

        AdminInquiryStatsDto phoneInquiryStats = callRepository.getStatsByAdminId(admin.getId());
        AdminInquiryStatsDto oneToOneInquiryStats = inquiryRepository.getStatsByAdminId(admin.getId());

        return AdminInfoResponse.from(admin ,phoneInquiryStats, oneToOneInquiryStats);
    }
}
