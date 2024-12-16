package com.hanaro.schedule_hanaro.admin.dto.response;

import com.hanaro.schedule_hanaro.global.domain.Admin;

public record AdminInfoResponse(
        Long adminId,
        AdminInfoDto adminInfo,
        AdminInquiryStatsDto phoneInquiryStats,
        AdminInquiryStatsDto oneToOneInquiryStats
) {
    public static AdminInfoResponse from(
            Admin admin,
            AdminInquiryStatsDto phoneInquiryStats,
            AdminInquiryStatsDto oneToOneInquiryStats
    ) {
        return new AdminInfoResponse(
                admin.getId(),
                AdminInfoDto.from(admin),
                phoneInquiryStats,
                oneToOneInquiryStats
        );
    }
}
