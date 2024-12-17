package com.hanaro.schedule_hanaro.admin.service;

import java.security.Principal;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.AdminRepository;
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

    public AdminInfoResponse getAdminStats(Authentication authentication) {
        Long id = PrincipalUtils.getId(authentication);
        Admin admin = adminRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_ADMIN));

        AdminInquiryStatsDto phoneInquiryStats = inquiryRepository.findStatsByAdminId(admin.getId());
        AdminInquiryStatsDto oneToOneInquiryStats = inquiryRepository.findStatsByAdminId(admin.getId());

        return AdminInfoResponse.from(admin ,phoneInquiryStats, oneToOneInquiryStats);
    }
}
