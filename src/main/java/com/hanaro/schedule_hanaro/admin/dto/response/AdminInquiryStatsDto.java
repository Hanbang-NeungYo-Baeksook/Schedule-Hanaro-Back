package com.hanaro.schedule_hanaro.admin.dto.response;

public record AdminInquiryStatsDto(
        int today,
        int weekly,
        int monthly,
        int total
) {
    public static AdminInquiryStatsDto of(int today, int weekly, int monthly, int total) {
        return new AdminInquiryStatsDto(today, weekly, monthly, total);
    }
}
