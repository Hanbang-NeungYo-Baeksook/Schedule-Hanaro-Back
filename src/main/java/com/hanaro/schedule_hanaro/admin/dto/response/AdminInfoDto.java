package com.hanaro.schedule_hanaro.admin.dto.response;

import com.hanaro.schedule_hanaro.global.domain.Admin;

public record AdminInfoDto(
        String name,
        String profileImage,
        String position
) {
    public static AdminInfoDto from(Admin admin) {
        return new AdminInfoDto(
                admin.getName(),
                "", // 프로필 이미지는 별도 컬럼으로 확장 가능
                admin.getRole().toString()
        );
    }
}
