package com.hanaro.schedule_hanaro.global.auth.dto.response;

public record AdminSignInResponse(
    String accessToken,
    String refreshToken,
    AdminInfo adminInfo
) {
    public record AdminInfo(
        Long adminId,
        String adminName,
        String branchName
    ) {}

    public static AdminSignInResponse of(
        JwtTokenDto tokenDto, 
        Long adminId,
        String adminName,
        String branchName
    ) {
        return new AdminSignInResponse(
            tokenDto.accessToken(),
            tokenDto.refreshToken(),
            new AdminInfo(adminId, adminName, branchName)
        );
    }
} 