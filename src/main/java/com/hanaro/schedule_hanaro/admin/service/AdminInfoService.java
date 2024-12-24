package com.hanaro.schedule_hanaro.admin.service;


import com.hanaro.schedule_hanaro.admin.dto.response.AdminInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryStatsDto;
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
    private final AdminCallService adminCallService;

    public AdminInfoResponse getAdminStats(Authentication authentication) {
        if (authentication == null) {
            throw new GlobalException(ErrorCode.FORBIDDEN_REQUEST, "인증 정보가 없습니다.");
        }

        Long id = PrincipalUtils.getId(authentication);
        if (id == null) {
            throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "관리자 ID가 필요합니다.");
        }

        Admin admin = adminRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_ADMIN));

        AdminInquiryStatsDto phoneInquiryStats = adminCallService.getStatsByAdminId(admin.getId());
        if (phoneInquiryStats == null) {
            throw new GlobalException(ErrorCode.NOT_FOUND_CALL, "전화 상담 통계를 찾을 수 없습니다.");
        }

        AdminInquiryStatsDto oneToOneInquiryStats = inquiryRepository.getStatsByAdminId(admin.getId());
        if (oneToOneInquiryStats == null) {
            throw new GlobalException(ErrorCode.NOT_FOUND_INQUIRY, "1:1 문의 통계를 찾을 수 없습니다.");
        }

        return AdminInfoResponse.from(admin, phoneInquiryStats, oneToOneInquiryStats);
    }
}
