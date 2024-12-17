package com.hanaro.schedule_hanaro.global.utils;

import java.security.Principal;

public class PrincipalUtils {
	public static String getAuthId(Principal principal) {
		if (principal == null || principal.getName() == null) {
			throw new IllegalArgumentException("Principal is null");
		}
		return principal.getName();
	}
}
