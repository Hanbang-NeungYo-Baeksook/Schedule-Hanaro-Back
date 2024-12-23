package com.hanaro.schedule_hanaro.global.utils;

import java.security.Principal;

import org.springframework.security.core.Authentication;

import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallHistoryResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminCallInfoResponse;
import com.hanaro.schedule_hanaro.admin.dto.response.AdminInquiryHistoryResponse;
import com.hanaro.schedule_hanaro.global.auth.info.CustomUserDetails;
import com.hanaro.schedule_hanaro.global.domain.Call;

public class PrincipalUtils {
	public static Long getId(Authentication authentication) {
		CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
		if (customUserDetails == null || customUserDetails.getUserId() == null) {
			throw new IllegalArgumentException("Principal is null");
		}
		return customUserDetails.getUserId();
	}
}
